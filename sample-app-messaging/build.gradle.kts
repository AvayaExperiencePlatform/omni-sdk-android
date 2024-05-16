plugins {
    id("com.android.application") version "8.3.2"
    id("org.jetbrains.kotlin.android") version "1.9.22"
    id("com.google.gms.google-services") version "4.4.1"
    id("com.google.devtools.ksp") version "1.9.22-1.0.18"
}

android {
    namespace = "com.avaya.axp.client.sample_app_messaging"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.avaya.axp.client.sample_app_messaging"
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
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
        buildConfig = true
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

dependencies {
    implementation("com.avaya.axp.client.sdk:core:0.1.0")
    implementation("com.avaya.axp.client.sdk:messaging:0.1.0")
    implementation("com.avaya.axp.client.sdk:messaging-ui:0.1.0")

    implementation(group = "androidx.activity", name = "activity-compose", version = "1.9.0")
    implementation(group = "androidx.compose.material", name = "material-icons-extended", version = "1.6.7")
    implementation(group = "androidx.compose.ui", name = "ui-graphics")
    implementation(group = "androidx.compose.ui", name = "ui-tooling-preview")
    implementation(group = "androidx.compose.ui", name = "ui")
    implementation(group = "androidx.compose", name = "compose-bom", version = "2024.05.00")
    implementation(group = "androidx.core", name = "core-ktx", version = "1.13.1")
    implementation(group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version = "2.7.0")
    implementation(group = "androidx.navigation", name = "navigation-compose", version = "2.7.7")
    implementation(group = "androidx.work", name = "work-runtime-ktx", version = "2.9.0")
    implementation(group = "com.google.android.material", name = "material", version = "1.12.0")

    implementation(group = "com.google.firebase", name = "firebase-common-ktx", version = "21.0.0")
    implementation(group = "com.google.firebase", name = "firebase-bom", version = "33.0.0")
    implementation(group = "com.google.firebase", name = "firebase-messaging", version = "24.0.0")

    implementation(group = "com.squareup.moshi", name = "moshi", version = "1.15.1")
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.15.1")
    ksp(group = "com.squareup.moshi", name = "moshi-kotlin-codegen", version = "1.15.1")
    implementation(group = "com.squareup.okhttp3", name = "logging-interceptor", version = "4.12.0")
    implementation(group = "com.squareup.retrofit2", name = "retrofit", version = "2.11.0")
    implementation(group = "com.squareup.retrofit2", name = "converter-moshi", version = "2.11.0")

    implementation(group = "com.github.tony19", name = "logback-android", version = "3.0.0")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.13")

    debugImplementation(group = "androidx.compose.ui", name = "ui-tooling")
    debugImplementation(group = "androidx.compose.ui", name = "ui-test-manifest", version = "1.6.7")
}
