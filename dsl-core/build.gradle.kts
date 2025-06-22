description = "dsl-core"

plugins {
    kotlin("jvm") version "2.0.21"
}

dependencies {
    api(project(":dsl-api"))
    api(libs.org.jetbrains.kotlin.kotlin.stdlib)
    api(libs.org.jetbrains.kotlin.kotlin.reflect)
    api(libs.software.amazon.awscdk.aws.cdk.lib)
}
