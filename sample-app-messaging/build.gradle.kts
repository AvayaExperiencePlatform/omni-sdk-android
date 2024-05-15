plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    `maven-publish`
}

val pomVersion = property("pom.version") as String

android {
    namespace = "com.avaya.axp.client.sample_app_messaging"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.avaya.axp.client.sample_app_messaging"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = providers.gradleProperty("pom.group.id").get()
            artifactId = "sample-app-messaging"
            version = pomVersion
            artifact("build/outputs/apk/release/${artifactId}-release-unsigned.apk")
            pom {
                name = "AXP Client SDK - Sample App Messaging"
                description = "AXP clients support for sample app messaging"
                // TODO: before really publishing, will need to fill in a license and URL here
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://nexus.forge.avaya.com/repository/metam-maven-release/"
            val snapshotsRepoUrl = "https://nexus.forge.avaya.com/repository/metam-maven-snapshot/"
            url = uri(
                if (pomVersion.toString()
                        .endsWith("SNAPSHOT")
                ) snapshotsRepoUrl else releasesRepoUrl
            )
            credentials {
                username = if (hasProperty("nexusUsername")) property("nexusUsername") as String else ""
                password = if (hasProperty("nexusPassword")) property("nexusPassword") as String else ""
            }
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":messaging"))
    implementation(project(":messaging-ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    implementation(libs.okhttp.logging)

    implementation(libs.logback.android)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.material.iconsext)
    implementation(libs.okhttp.logging)

    implementation(libs.firebase.common.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.work)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
