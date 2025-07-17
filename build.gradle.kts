tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}

plugins {
    alias(libs.plugins.kotlin) apply false // Kotlin support
}

subprojects {
    plugins.apply("org.jetbrains.kotlin.jvm")
}
