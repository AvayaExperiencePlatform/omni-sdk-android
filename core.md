# Module AXP Core

## Overview

The AXP Core module is the fundamental base of the Android version of the AXP
Client SDK.

The main responsibility of the core is to manage sessions with AXP. It provides
the essential logic for configuring the SDK and making REST API calls to AXP.
However, this module is not standalone and works in conjunction with two other
optional modules:

1. **AxpMessaging**: Enables text messaging to agents in AXP.
2. **AxpCalling**: Enables voice calls with agents in AXP over the Internet
   using WebRTC.

Developers can choose to include only the modules needed for their application
and omit the others if their functionality is not required. Note that the Core
module is always required.

## Prerequisites

To use this SDK, you need an account registered with the Avaya Experience
Platform™, and have that account provisioned to enable use of the client APIs.

Once you have an account, it must be provisioned for the following two items:

1. **Integration ID**

   To create an integration, follow the instructions in [Creating an Omni SDK
   Integration][omni-integration]. The two services you can enable there
   (**Messaging** and **WebRTC Voice**) each correspond to a client SDK module,
   and you must enable the services for the modules that you will use.

   Note the integration ID that is created, as you will need to provide it when
   configuring the SDK as described below.

2. **Application Key**

   To enable remote access to the AXP APIs, you need to get an application key
   as described in [How to Authenticate with Avaya Experience Platform™
   APIs][axp-auth].

   Note the application key that is created, as you will need to provide it when
   configuring the SDK as described below.

## Installation

The AXP Core module is distributed via the Maven registry in GitHub Packages.

### Maven Installation

If you have a GitHub account, you can use it to download the package
automatically from the registry.

#### Generate a Personal Access Token

To download packages from the GitHub registry, you first need to generate an
authentication token for your GitHub account.

To generate one, follow the instructions from [Creating a personal access token
(classic)][gh-token]. For the selected scopes, pick "read:packages".

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
}
```

or Kotlin `build.gradle.kts` file:

```kotlin
// For Kotlin DSL
dependencies {
    implementation("com.avaya.sdk:core:${avayaSdkVersion}")
}
```

Replace `${avayaSdkVersion}` with the latest version of the AXP SDK.

### Manual Installation

If you don't have or wish to use a GitHub account, you can download the package
manually from [its package page][package].

#### Include Package

To include the package in your project, add the following to your `build.gradle`
file:

```groovy
// For Groovy
dependencies {
    implementation files('${path}/core-${avayaSdkVersion}.aar')
}
```

or Kotlin `build.gradle.kts` file:

```kotlin
// For Kotlin DSL
dependencies {
    implementation(files("${path}/core-${avayaSdkVersion}.jar.aar"))
}
```

Replace `${avayaSdkVersion}` with the version number of the AXP SDK and
`${path}` with the directory you put the downloaded package file in.

## SDK Configuration

Before using any SDK functionality, it needs to be configured using the
`AxpClientSdk.configureSdk` method.

### Configuration Parameters

- `applicationContext`: Your Android application's context. It must be the
  application context, and can not be an activity or service context.
- `host`: The hostname of the AXP API endpoint to connect to. You can also pass
  in an `AxpApiRegion` instance for the AXP geographic region of your account.
- `appKey`: The application key for your tenant integration configured on AXP,
  as described in the *Prerequisites* section above.
- `integrationId`: The integration ID for the tenant that is configured on AXP,
  as described in the *Prerequisites* section above.
- `jwtProvider`: An instance implementing the `JwtProvider` interface to be used
  in authenticating the client with AXP.
- `configMap`: An optional map of additional configuration items. See the
  `SdkConfigKey` enumerated type for the possible keys in this map and their
  possible values. The contents of this map are checked at runtime for type
  correctness, and an exception will be thrown if a value is not of the expected
  type for its key.

Here's an example of SDK configuration for an application in North America:

```kotlin
AxpClientSdk.configureSdk(
    applicationContext = context,
    host = AxpApiRegion.NORTH_AMERICA,
    appKey = MY_APP_KEY,
    integrationId = MY_INTEGRATION_ID,
    jwtProvider = MyJwtProvider(),
    configMap = mapOf(
        SdkConfigKey.USER_AGENT to MY_USER_AGENT,
        SdkConfigKey.DISPLAY_NAME to "My User Name"
    )
)
```

## Authentication

The AXP Client SDK uses JSON Web Tokens (JWT) for client authentication. The JWT
is obtained from your own web application that communicates with AXP's
authentication API.

### Implementing the JwtProvider Interface

To provide the SDK with the JWT, you need to implement the `JwtProvider`
interface in your application. This interface has a single method, `fetchJwt`,
which is a suspending function that returns a JWT.

In your implementation of `fetchJwt`, you should connect to your web application,
which in turn talks to AXP to get the JWT. If there is any failure in getting
the JWT, return `null` and the corresponding HTTP request will fail with an
authentication error.

Here is an example of how you might implement this interface:

```kotlin
class MyJwtProvider : JwtProvider {
    override suspend fun fetchJwt(): String? {
        // Connect to your web application here
        // If successful, return the JWT
        // If not, return null
    }
}
```

Once you have implemented the `JwtProvider` interface, you pass an instance of
your implementation to the `AxpClientSdk.configureSdk` method as part of the
configuration process.

## Asynchronous Operation

For every API call that involves asynchronous operation (such as making network
requests to AXP), the AXP Client SDK provides two versions for Android:

1. **Kotlin Coroutines**: These are operations that are defined as `suspend fun`
   in Kotlin. They are designed to be used from Kotlin coroutines for handling
   asynchronous tasks. When these operations complete, they return an instance
   of the `AxpResult` type, which wraps a success value or an error.
2. **Async Callbacks**: These are operations that are defined without the
   `suspend` keyword. They are designed for use by applications that are not
   using Kotlin coroutines (e.g. apps written in Java). These operations take an
   additional parameter, which is an application-supplied instance of the
   `ResponseHandler` callback type. When the operation completes, the supplied
   callback will be invoked with either the success value or an error object.

### Example: getDefaultConversation

Here's an example of how you might use both types of asynchronous operations
with the `getDefaultConversation` method in the AXP SDK:

#### Using Kotlin Coroutines

```kotlin
launch {
    when (val result = AxpClientSdk.getDefaultConversation()) {
        is AxpResult.Success -> {
            // Handle success
            val conversation = result.value
        }
        is AxpResult.Failure -> {
            // Handle failure
            val error = result.error
        }
    }
}
```

#### Using Async Callbacks

```kotlin
AxpClientSdk.getDefaultConversation(object : ResponseHandler<Conversation> {
    override fun onSuccess(result: Conversation) {
        // Handle success
    }

    override fun onFailure(error: AxpError) {
        // Handle failure
    }
})
```

## AxpClientSdk Methods

The AXP Client SDK provides the following class methods that apply to the SDK as
a whole.

### Getting the Conversation

The `getDefaultConversation` returns a `Conversation` instance that can be used
with the other modules for a chat message session or voice call. If there is not
currently a conversation, a new one will be created.

```kotlin
suspend fun getDefaultConversation(): AxpResult<out Conversation>

fun getDefaultConversation(responseHandler: ResponseHandler<Conversation>)
```

### Terminating the SDK

The `shutDown` method is used to terminate the current user session. After
calling this, the SDK can no longer be used until it is reconfigured by calling
`configureSdk` again.

```kotlin
suspend fun shutDown(): AxpResult<Unit>

fun shutDown(responseHandler: ResponseHandler<Unit>)
```

This method does not take any parameters. There is effectively no result value,
but if there is an error it will be communicated via the `AxpResult` or
`ResponseHandler`. Even if an error occurs, the SDK is terminated and can not be
used afterwards.

# Package com.avaya.axp.client.sdk

[omni-integration]: https://documentation.avaya.com/bundle/ExperiencePlatform_Administering_10/page/Creating_an_Omni_SDK_integration.html
[axp-auth]: https://developers.avayacloud.com/avaya-experience-platform/docs/how-to-authenticate-with-axp-apis
[gh-token]: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic
[package]: https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150727
