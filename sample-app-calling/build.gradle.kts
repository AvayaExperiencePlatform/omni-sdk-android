plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    `maven-publish`
}

val pomVersion = property("pom.version") as String

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

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = providers.gradleProperty("pom.group.id").get()
            artifactId = "sample-app-calling"
            version = pomVersion
            artifact("build/outputs/apk/release/${artifactId}-release-unsigned.apk")
            pom {
                name = "AXP Client SDK - Sample app calling"
                description = "AXP clients support for sample app calling"
                // TODO: before really publishing, will need to fill in a license and URL here
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://nexus.forge.avaya.com/repository/metam-maven-release/"
            val snapshotsRepoUrl = "https://nexus.forge.avaya.com/repository/metam-maven-snapshot/"
            url = uri(
                if (pomVersion.endsWith("SNAPSHOT")) {
                    snapshotsRepoUrl
                } else {
                    releasesRepoUrl
                }
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
    implementation(project(":mpaas-calling"))
    implementation(project(":webrtc"))

    coreLibraryDesugaring(libs.core.desugaring)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.telecom)

    // Compose
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.material)   // TODO: this should be removed once theming problem is resolved
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.compose.material.iconsext)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.logback.android)
    implementation(libs.slf4j)
}
