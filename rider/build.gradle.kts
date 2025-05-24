import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import java.io.ByteArrayOutputStream

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
}

group = providers.gradleProperty("pluginGroupRD").get()
version = providers.gradleProperty("pluginVersion").get()

val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)
extra["isWindows"] = isWindows

val dotnetSolution: String by project
val buildConfiguration: String by project
val dotnetPluginId: String by project

val buildToolExecutable: String by lazy {
    if (isWindows) {
        val paths = System.getenv("PATH").split(";")

        paths.forEach {
            val msbuild = File("$it\\MSBuild.exe")
            if (msbuild.exists()) {
                return@lazy msbuild.absolutePath
            }
        }

        val stdout = ByteArrayOutputStream()
        serviceOf<ExecOperations>().exec {
            executable("${project.projectDir}/tools/vswhere.exe")
            args("-latest", "-property", "installationPath", "-products", "*")
            standardOutput = stdout
        }
        val directory = stdout.toString().trim()
        if (directory.isNotEmpty()) {
            File(directory).walkTopDown()
                .filter { it.name == "MSBuild.exe" }
                .map { it.absolutePath }
                .firstOrNull() ?: "msbuild"
        } else {
            "msbuild"
        }
    } else {
        "dotnet"
    }
}

val buildToolArgs: List<String> by lazy {
    mutableListOf<String>().apply {
        add(if (isWindows) "/v:minimal" else "msbuild")
        add(dotnetSolution)
        add("/p:Configuration=$buildConfiguration")
        add("/p:HostFullIdentifier=")
    }
}

repositories {
    mavenCentral()
    maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }

    intellijPlatform {
        defaultRepositories()
    }
}

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(21)
}

val compileDotNet by tasks.registering {
    doLast {
        val executable = buildToolExecutable
        val arguments = buildToolArgs.toMutableList()
        arguments.add("/t:Restore;Rebuild")
        serviceOf<ExecOperations>().exec {
            executable(executable)
            args(arguments)
            workingDir(projectDir)
        }
    }
}

val testDotNet by tasks.registering {
    doLast {
        serviceOf<ExecOperations>().exec {
            executable("dotnet")
            args("test", dotnetSolution, "--logger", "GitHubActions")
            workingDir(rootDir)
        }
    }
}

dependencies {
    intellijPlatform {
        rider(providers.gradleProperty("platformVersionRD"), false)

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPluginsRD").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPluginsRD").map { it.split(',') })

        pluginVerifier()
        zipSigner()
    }
}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion")
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

tasks {
    buildPlugin {
        doLast {
            copy {
                from("${layout.buildDirectory}/distributions/${project.name}-${version}.zip")
                into("${projectDir}/output")
            }

            val executable = buildToolExecutable
            val arguments = buildToolArgs.toMutableList()
            arguments.add("/t:Pack")
            arguments.add("/p:PackageOutputPath=${projectDir}/output")
            arguments.add("/p:PackageVersion=${version}")
            serviceOf<ExecOperations>().exec {
                executable(executable)
                args(arguments)
                workingDir(projectDir)
            }
        }
    }

    runIde {
        // Match Rider's default heap size of 1.5Gb (default for runIde is 512Mb)
        maxHeapSize = "1500m"
    }

    prepareSandbox {
        dependsOn(compileDotNet)

        val outputFolder = "${projectDir}/src/dotnet/${dotnetPluginId}/bin/${buildConfiguration}"
        val dllFiles = listOf(
            "$outputFolder/${dotnetPluginId}.dll",
            "$outputFolder/${dotnetPluginId}.pdb",
        )

        dllFiles.forEach { f ->
            val file = file(f)
            from(file) { into("${project.name}/dotnet") }
        }

        doLast {
            dllFiles.forEach { f ->
                val file = file(f)
                if (!file.exists()) throw RuntimeException("File $file does not exist")
            }
        }
    }
}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }

            plugins {
                robotServerPlugin()
            }
        }
    }
}
