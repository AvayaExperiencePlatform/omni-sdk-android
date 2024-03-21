# AXP Core
## Overview

The AXP Core module provides the basic functionality to configure the SDK and retrieve the default conversation of the user.

## Installation

Add this core dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation files('${path}/core.jar')
}
```

If you're using Kotlin DSL, add:

```kotlin
dependencies {
    implementation(files("${path}/core.jar"))
}
```

In both cases, replace ${path} with the original path of jar. You can find the latest version on the [AXP SDK releases page](./omni-sdk/core-0.0.1.jar).


Replace `${avayaSdkVersion}` with the latest version of the AXP SDK.

## SDK Configuration

Before using any SDK functionality, it needs to be configured using the `AxpClientSdk.configureSdk`
method. The configuration parameters include:

1. One that takes a region parameter, which is an enumerated type defining the various geographic
   regions around the world in which AXP is provided. The region closest to the developer's location
   should be used.
2. Another that takes a hostname parameter, which is the hostname of the AXP API endpoint to connect
   to. This is primarily intended for internal Avaya use, because it allows connecting to
   development
   labs and not just the production environments.

### Configuration Parameters

- `host`: Hostname of the AXP API endpoint to connect to or an AXP region code. If a region code is provided (e.g., "na" for North America), it will be converted to the corresponding hostname (e.g., "na.cc.avayacloud.com"). If a full hostname is provided (e.g., "axp-dev.avayacloud.com"), it will be used as is.
- `appKey`: Application Key which is required to access the API for your Account.
- `integrationId`: The integration ID for the tenant that is configured on AXP.
- `jwtProvider`: Application-provided instance that fetches a JWT for authentication from AXP.
- `configMap`: Optional map of additional configuration. See [SdkConfigKey] for the possible entries.

Here's an example of SDK configuration for an application in North America:

```kotlin
AxpClientSdk.configureSdk(
    host = AxpApiRegion.NORTH_AMERICA,
    appKey = MY_APP_KEY,
    integrationId = MY_INTEGRATION_ID,
    jwtProvider = MyJwtProvider(),
    configMap = mapOf(
        SdkConfigKey.USER_AGENT to MY_USER_AGENT,
        SdkConfigKey.APPLICATION_CONTEXT to applicationContext
    )
)
```

or you can even use

```kotlin
AxpClientSdk.configureSdk(
    host = HOST_NAME,
    integrationId = MY_INTEGRATION_ID,
    jwtProvider = MyJwtProvider(),
    configMap = mapOf(
        SdkConfigKey.USER_AGENT to MY_USER_AGENT,
        SdkConfigKey.APPLICATION_CONTEXT to applicationContext
    )
)
```

## Authentication

The AXP Client SDK uses JSON Web Tokens (JWT) for client authentication. The JWT is obtained from
your own web application that communicates with AXP's authentication API.  
Implementing the JwtProvider Interface

To provide the SDK with the JWT, you need to implement the JwtProvider interface in your
application. This interface has a single method, fetchJwt, which is a suspending function that
returns a JWT.
In your implementation of fetchJwt, you should connect to your web application, which in turn talks
to AXP to get the JWT. If there is any failure in getting the JWT, return null and the corresponding
HTTP request will fail with an authentication error. Here is an example of how you might implement
this interface:

```kotlin
class MyJwtProvider : JwtProvider {
    override suspend fun fetchJwt(): String? {
        // Connect to your web application here
        // If successful, return the JWT
        // If not, return null
    }
}
```

Once you have implemented the JwtProvider interface, you can pass an instance of your implementation
to the AxpClientSdk.configureSdk method:

## Asynchronous Operation

The AXP SDK provides two types of asynchronous operations:

1. **Kotlin Coroutines**: These are operations that are defined as suspend fun in Kotlin. They are
   designed
   to be used with Kotlin's built-in coroutines feature for handling asynchronous tasks. When these
   operations complete, they return an instance of the AxpResult type.
2. **Async Callbacks**: These are operations that have "Async" appended to their name. They are
   designed
   for use by applications that are not using Kotlin coroutines. These operations take an additional
   parameter, which is an application-supplied instance of the ResponseHandler callback type.

### Example: getDefaultConversation

Here's an example of how you might use both types of asynchronous operations with the
getDefaultConversation method in the AXP SDK:

#### Using Kotlin Coroutines

```kotlin
launch {
    when (val conversationResult = AxpClientSdk.getDefaultConversation()) {
        is AxpResult.Success -> {
            // Handle success
            val conversation = conversationResult.value
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
AxpClientSdk.getDefaultConversation(object : ResponseHandler<UserSession> {
    override fun onSuccess(conversationResult: Conversation) {
        // Handle success
        val conversation = conversationResult.value
    }

    override fun onFailure(error: AxpSdkError) {
        // Handle failure
    }
})
```

## Session Management

### getDefaultConversation

The `getDefaultConversation` method is used to get the default conversation.

This method throws a SdkNotConfiguredException if configureSdk has not previously been called.

|Property|Description|
| --| -- |
|contextParameters| Optional key/value context information. This information might be used to make business decisions on how the dialog is treated. For example, the client application might want to send hints about customer's interest based on the customer's searches or FAQ browsing.|
|conversationId| Unique Identifier for the conversation|
|participants|The current Participants in the conversation across all channel types.|
|participantsFlow| `Flow` to observe for when the set of participants changes across all channel types.|

 |Methods| Description|
 |--|--|
 |getParticipantById(participantId)|Returns the Participant in the conversation with the given ID.|
 |participants(channel: AxpChannel)|Returns a set of `Participants` that are part of the conversation in the specified channel.|
 |addParticipantsChangedListener(listener: ParticipantsChangedListener)| For Java applications that can't directly observe a Kotlin Flow, use this to register a listener to be called when the set of participants in the conversation changes.|
|removeParticipantsChangedListener(listener:ParticipantsChangedListener)| Removes the attached listener from the conversation|




### Shutdown SDK

The `shutDown` method is used to terminate the current user session. Irrespective of the success or failure of the termination operation, the SDK cleanup will be performed.

```kotlin
AxpClientSdk.shutDown()
```
