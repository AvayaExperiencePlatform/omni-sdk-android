## Introduction

This guide serves as an example for implementing the core, messaging, and messaging UI SDKs with minimum requirements. It demonstrates how to seamlessly integrate a messaging interface into your application, utilizing these three libraries.

## Integration Steps

To seamlessly incorporate the Messaging UI SDK into your application, follow these straightforward integration steps:

1. **Add SDK Files to Your Project:**
    - include the `.jar` and `.aar` files in your project. Here we have included it in omni-sdk folder.

2. **Implement Dependencies:**
    - In your app-level `build.gradle` file, add the following dependencies:

```groovy
   dependencies {
       implementation files('{path}/core.aar')
       implementation files('{path}/messaging.aar')
       implementation files('{path}/messaging-ui.aar')
   }
```

kotlinDSL
```kotlinDSL
   dependencies {
       implementation (files("{path}/core.aar"))
       implementation (files("{path}/messaging.aar"))
       implementation (files("{path}/messaging-ui.aar"))
   }
```
Replace {path} with the absolute path to the .aar files.

## Required Configurations

### Include Additional Dependencies:

Integrate the following dependencies in app which will be utilized by sdks:
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
- `AUTH_BASE_URL` = replace with token server url
- `USER_ID` = replace with customer user id
- `USER_NAME` = replace with customer user name
- `CONTEXT_PARAMETERS` = replace with map of context parameters
- `AXP_BASE_URL` = replace with AXP server url
- `AXP_APP_KEY`= replace with AXP app key
- `AXP_INTEGRATION_ID` = replace with AXP integration id

## Running the App
After making these changes, you're ready to run the app.
