plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "surik.simyan.aliasika.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "surik.simyan.aliasika.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.3.2")
    implementation("androidx.compose.ui:ui-tooling:1.3.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.2")
    implementation("androidx.compose.foundation:foundation:1.3.1")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.activity:activity-compose:1.6.1")

    // ------- Added by me -------

    // Koin
    val koinVersion = "3.3.0"
    val koinAndroidVersion = "3.3.1"
    val koinAndroidComposeVersion = "3.4.0"
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinAndroidVersion")
    implementation("io.insert-koin:koin-androidx-navigation:$koinAndroidVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinAndroidComposeVersion")

    // Wheel Picker
    implementation("com.github.zj565061763:compose-wheel-picker:1.0.0-alpha10")

    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.3.1")
    implementation("androidx.compose.material:material-icons-extended:1.3.1")

    // Tinder swipe
    implementation("com.github.theapache64:twyper:0.0.4")
}
