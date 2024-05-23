## Introduction

This guide serves as an example for implementing the core, messaging, and messaging UI SDKs with minimum requirements. It demonstrates how to integrate a messaging interface into your application seamlessly, utilizing these three libraries.

## Installation

The AXP Messaging UI library is distributed via the Maven registry in GitHub
Packages.

### Maven Installation

If you have a GitHub account, you can use it to download the package
automatically from the registry.

#### Generate a Personal Access Token

To download packages from the GitHub registry, you first need to generate an
authentication token for your GitHub account.

To generate one, follow the instructions from [Creating a personal access token
(classic)]
(https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic)
For the selected scopes, pick "read:packages".

#### Add Repository

To access the AXP SDK repository, add the following to your `build.gradle` or
`settings.gradle` file:

```groovy
// For Groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/AvayaExperiencePlatform/omni-sdk-android")
        credentials {
            username = "<GITHUB-ACCOUNT>"
            password = "<GITHUB-TOKEN>"
        }
    }
}
```

or if using the Kotlin DSL, `build.gradle.kts` or `settings.gradle.kts` file:

```kotlin
// For Kotlin DSL
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/AvayaExperiencePlatform/omni-sdk-android")
        credentials {
            username = "<GITHUB-ACCOUNT>"
            password = "<GITHUB-TOKEN>"
        }
    }
}
```

replacing `<GITHUB-ACCOUNT>` with your GitHub user ID and `<GITHUB-TOKEN>` with
the token generated in the previous step.

#### Include Package

To include the package in your project, add the following to your `build.gradle`
file:

```groovy
// For Groovy
dependencies {
    implementation 'com.avaya.sdk:core:${avayaSdkVersion}'
    implementation 'com.avaya.sdk:messaging:${avayaSdkVersion}'
    implementation 'com.avaya.sdk:messaging-ui:${avayaSdkVersion}'
}
```

or Kotlin `build.gradle.kts` file:

```kotlin
// For Kotlin DSL
dependencies {
    implementation("com.avaya.sdk:core:${avayaSdkVersion}")
    implementation("com.avaya.sdk:messaging:${avayaSdkVersion}")
    implementation("com.avaya.sdk:messaging-ui:${avayaSdkVersion}")
}
```

Replace `${avayaSdkVersion}` with the latest version of the AXP SDK.

### Manual Installation

If you don't have or wish to use a GitHub account, you can download the package
manually from [its package page]
(https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150733)

You'll also need to download the [Core module]
(https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150727)
and [Messaging Module]
(https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150732)
that it depends on.

#### Include Package

To include the package in your project, add the following to your `build.gradle`
file:

```groovy
// For Groovy
dependencies {
    implementation files('${path}/core-${avayaSdkVersion}.aar')
    implementation files('${path}/messaging-${avayaSdkVersion}.aar')
    implementation files('${path}/messaging-ui-${avayaSdkVersion}.aar')
}
```

or Kotlin `build.gradle.kts` file:

```kotlin
// For Kotlin DSL
dependencies {
    implementation(files("${path}/core-${avayaSdkVersion}.jar.aar"))
    implementation(files("${path}/messaging-${avayaSdkVersion}.jar.aar"))
    implementation(files("${path}/messaging-ui-${avayaSdkVersion}.jar.aar"))
}
```

Replace `${avayaSdkVersion}` with the version number of the AXP SDK and
`${path}` with the directory you put the downloaded package files in.

## Required Configurations

### Include Additional Dependencies:

Integrate the following dependencies into the app, which will be utilized by the SDKs:

- Groovy
```groovy
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-moshi:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.moshi:moshi:1.15.1'
    implementation 'com.squareup.moshi:moshi-kotlin:1.15.1'
    implementation 'org.slf4j:slf4j-api:2.0.9'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.work:work-runtime-ktx:2.9.0'
    implementation 'io.coil-kt:coil-compose:2.2.2'
    implementation 'io.coil-kt:coil-gif:2.4.0'
    implementation platform('androidx.compose:compose-bom:2024.02.02')
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation 'androidx.compose.material:material-icons-extended'
    implementation 'androidx.compose.runtime:runtime-livedata:1.6.3'
    implementation 'androidx.compose.ui:ui-tooling-preview'
}
```

- kotlinDSL
```kotlinDSL
   dependencies {
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("com.squareup.moshi:moshi:1.15.1")
        implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
        implementation("org.slf4j:slf4j-api:2.0.9")
   
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.work:work-runtime-ktx:2.9.0")
        implementation("io.coil-kt:coil-compose:2.2.2")
        implementation("io.coil-kt:coil-gif:2.4.0")
        implementation(platform("androidx.compose:compose-bom:2024.02.02"))
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.activity:activity-compose:1.8.2")
        implementation("androidx.compose.material:material-icons-extended")
        implementation("androidx.compose.runtime:runtime-livedata:1.6.3")
        implementation("androidx.compose.ui:ui-tooling-preview")
   }
```

### Updating values in [CONSTANTS.kt](./src/main/java/com/avaya/axp/client/sample_app_messaging/util/Constants.kt) file
In constants file, update the following values:
- `TOKEN_PROVIDER_URL` = replace with token server URL
- `USER_ID` = replace with customer user ID
- `USER_NAME` = replace with customer user name
- `CONTEXT_PARAMETERS` = replace with map of context parameters used for routing to agent
- `AXP_HOSTNAME` = replace with AXP server URL
- `AXP_APP_KEY`= replace with AXP app key
- `AXP_INTEGRATION_ID` = replace with AXP integration ID
- `AXP_CONFIG_ID` = replace with AXP config ID for push notifications
- `NOTIFICATION_BASE_URL` = replace with notification server URL if you are using push notifications

### Push Notifications
- To enable push notifications follow the steps below:
    - Generate and add google-services.json file in the app module of your project.
    - Create a conifig ID by following the steps mentioned in following link: https://developers.avayacloud.com/avaya-experience-platform/docs/omni-sdk-push-notifications
    - Add the config ID in [CONSTANTS.kt](./src/main/java/com/avaya/axp/client/sample_app_messaging/util/Constants.kt) file
    - In MessagingSdkViewModel.kt file, uncomment the line `SdkConfigKey.PUSH_CONFIG_ID to AXP_CONFIG_ID` in `configureSdk(context: Context)` function
    - You need to save config at fcm connector by following the steps mentioned in following link: https://github.com/AvayaExperiencePlatform/omni-sdk-starter-kit/blob/master/%20sample-fcm-push-notification-server/README.md
    - Device registration and other things will be handled by sample app

## Running the App
After making these changes, you're ready to run the app.
