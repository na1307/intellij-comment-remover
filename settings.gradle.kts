plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "intellij-comment-remover"

include("intellij")
project(":intellij").name = "comment-remover"

include("rider")
project(":rider").name = "comment-remover-csvbcpp"
