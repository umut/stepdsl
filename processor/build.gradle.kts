description = "processor"

plugins {
    kotlin("jvm") version "2.0.21"
}

dependencies {
    api(project(":dsl-api"))
    api(libs.org.jetbrains.kotlin.kotlin.stdlib)
    api(libs.com.google.devtools.ksp.symbol.processing.api)
    api(libs.com.squareup.kotlinpoet)
    api(libs.com.squareup.kotlinpoet.ksp)
}
