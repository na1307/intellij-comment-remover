plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "intellij-comment-remover"

include("intellij")
project(":intellij").name = "comment-remover"
