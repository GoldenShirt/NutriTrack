plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.nutritrack" // Add this line
    compileSdk = 34
    defaultConfig {
        applicationId = "com.nutritrack"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    kotlinOptions {
        jvmTarget = "1.8" // Match this with your Java version
    }
    kotlin {
        jvmToolchain(17) // Specify the JDK version you want to use
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Or your desired Java version
        targetCompatibility = JavaVersion.VERSION_1_8 // Or your desired Java version
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.camera.core)
    kapt("androidx.room:room-compiler:2.6.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Retrofit + Moshi (לקריאות API ל‑OpenAI)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    //Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    // Accompanist Permissions (ל‑Whisper רקע)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Charts (קלי  משקל):
    implementation("com.github.tehras:charts:0.2.4-alpha")}
