plugins {
    // trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.3.1").apply(false)
    id("com.android.library").version("7.3.1").apply(false)
    kotlin("android").version("1.7.10").apply(false)
    kotlin("multiplatform").version("1.7.10").apply(false)
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("dev.icerock.moko:resources-generator:0.20.1")
        classpath("dev.icerock.moko:kswift-gradle-plugin:0.6.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink>()
    .matching { it.binary is org.jetbrains.kotlin.gradle.plugin.mpp.Framework }
    .configureEach {
        doLast {
            val kSwiftGeneratedDir = destinationDirectory.get()
                .dir("${binary.baseName}Swift")
                .asFile

            val kSwiftPodSourceDir = buildDir
                .resolve("cocoapods")
                .resolve("framework")
                .resolve("${binary.baseName}Swift")

            kSwiftGeneratedDir.copyRecursively(kSwiftPodSourceDir, overwrite = true)
            println("[COPIED] $kSwiftGeneratedDir -> $kSwiftPodSourceDir")
        }
    }
