plugins {
    id("com.android.application") version "8.3.2"
    id("org.jetbrains.kotlin.android") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.18"
}

android {
    namespace = "com.avaya.axp.client.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.avaya.axp.client.sample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("com.avaya.axp.client.sdk:core:0.1.1")
    implementation("com.avaya.axp.client.sdk:mpaas-calling:0.1.1")
    implementation("com.avaya.axp.client.sdk:calling:0.1.1")

    coreLibraryDesugaring(group = "com.android.tools", name = "desugar_jdk_libs", version = "2.0.4")
    implementation(group = "androidx.core", name = "core-ktx", version = "1.13.1")
    implementation(group = "androidx.datastore", name = "datastore-preferences", version = "1.1.1")
    implementation(group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version = "2.7.0")
    implementation(group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version = "2.7.0")
    implementation(group = "androidx.core", name = "core-telecom", version = "1.0.0-alpha03")

    // Compose
    implementation(group = "com.google.accompanist", name = "accompanist-permissions", version = "0.34.0")
    implementation(group = "androidx.activity", name = "activity-compose", version = "1.9.0")
    implementation(group = "androidx.compose", name = "compose-bom", version = "2024.05.00")
    implementation(group = "androidx.compose.ui", name = "ui")
    implementation(group = "androidx.compose.ui", name = "ui-tooling-preview")
    implementation(group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version = "2.7.0")
    implementation(group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version = "2.7.0")
    implementation(group = "com.google.android.material", name = "material", version = "1.12.0")
    implementation(group = "androidx.compose.material3", name = "material3", version = "1.2.1")
    implementation(group = "androidx.navigation", name = "navigation-compose", version = "2.7.7")
    implementation(group = "androidx.compose.material", name = "material-icons-extended", version = "1.6.7")
    debugImplementation(group = "androidx.compose.ui", name = "ui-test-manifest", version = "1.6.7")
    debugImplementation(group = "androidx.compose.ui", name = "ui-tooling")

    implementation(group = "com.squareup.moshi", name = "moshi", version = "1.15.1")
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.15.1")
    ksp(group = "com.squareup.moshi", name = "moshi-kotlin-codegen", version = "1.15.1")
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.12.0")
    implementation(group = "com.squareup.okhttp3", name = "logging-interceptor", version = "4.12.0")

    implementation(group = "com.github.tony19", name = "logback-android", version = "3.0.0")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.13")
}
