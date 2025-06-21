plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

dependencies {
    api(project(":dsl-core"))
    api(libs.org.jetbrains.kotlin.kotlin.stdlib.x1)
    api(libs.org.jetbrains.kotlin.kotlin.reflect.x1)
    api(libs.com.google.devtools.ksp.symbol.processing.api)
    api(libs.com.squareup.kotlinpoet)
    api(libs.com.squareup.kotlinpoet.ksp)
    api(libs.software.constructs.constructs)
    api(libs.software.amazon.awscdk.aws.cdk.lib)
    testImplementation(libs.org.jetbrains.kotlin.kotlin.test)
    ksp(project(":processor"))
}

description = "example-workflow"
