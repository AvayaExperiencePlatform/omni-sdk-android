# Module AXP Messaging

The AXP Messaging module enables text messaging to agents in AXP. It works in
conjunction with the AXP Core module, which establishes the necessary connections.

This enables asynchronous communication, allowing end users to resume
conversation threads at any time and view all previous messages exchanged as
part of the conversation. This is unlike a session-based chat, where the chat is
closed after the participants disconnect the dialog.

## Prerequisites

To use the Messaging module, you need an Omni SDK integration provisioned with
the **Messaging** service enabled. Follow the instructions in
[Creating an Omni SDK Integration][omni-integration] to set up an integration
with messaging support and use the integration ID for configuring the SDK as
described in the documentation for the Core module.

## Installation

The AXP Messaging module is distributed via the Maven registry in GitHub Packages.

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
    implementation 'com.avaya.sdk:messaging:${avayaSdkVersion}'
}
```

or Kotlin `build.gradle.kts` file:

```kotlin
// For Kotlin DSL
dependencies {
    implementation("com.avaya.sdk:core:${avayaSdkVersion}")
    implementation("com.avaya.sdk:messaging:${avayaSdkVersion}")
}
```

Replace `${avayaSdkVersion}` with the latest version of the AXP SDK.

### Manual Installation

If you don't have or wish to use a GitHub account, you can download the package
manually from [its package page][package].

You'll also need to download the [Core module][core-package] that it depends on.

#### Include Package

To include the package in your project, add the following to your `build.gradle`
file:

```groovy
// For Groovy
dependencies {
    implementation files('${path}/core-${avayaSdkVersion}.aar')
    implementation files('${path}/messaging-${avayaSdkVersion}.aar')
}
```

or Kotlin `build.gradle.kts` file:

```kotlin
// For Kotlin DSL
dependencies {
    implementation(files("${path}/core-${avayaSdkVersion}.jar.aar"))
    implementation(files("${path}/messaging-${avayaSdkVersion}.jar.aar"))
}
```

Replace `${avayaSdkVersion}` with the version number of the AXP SDK and
`${path}` with the directory you put the downloaded package files in.

## Usage

### Conversation

Before you can send a message, you need a valid `Conversation`. You can obtain
this by calling `AxpClientSdk.getDefaultConversation()` from the AXP Core module.

Here's an example of getting the default conversation:

```kotlin
launch {
    when (val result = AxpClientSdk.getDefaultConversation()) {
        is AxpResult.Success -> {
            val defaultConversation = result
            // Use the userSession to send a message
        }
        is AxpResult.Failure -> {
            val error = result.error
            // Handle the error
        }
    }
}
```

### Sending a Message

Once you have a valid `Conversation`, you can use it to send messages.

The AXP Messaging module supports various types of messages, including:

- Text (Plain text, Emojis, Links)
- Postback
- Reply
- Attachment
- Location

To link a message to a previous message in the thread, each of the methods for
sending a message has an optional parameter for the message ID of the parent
message.

#### Sending a Plain Text Message

```kotlin
val message = "Hello, how can I help you?"
conversation.sendMessage(message)
```

#### Sending a Location Message

A location message includes latitude and longitude coordinates.

```kotlin
// LOCATION_NAME, ADDRESS_LINE, PARENT_MESSAGE_ID are optional
conversation.sendMessage(
    LocationMessage(latitude, longitude, LOCATION_NAME, ADDRESS_LINE),
    PARENT_MESSAGE_ID
)
```

#### Sending a Postback Message

```kotlin
// PARENT_MESSAGE_ID is optional
conversation.sendMessage(PostbackMessage(text, payload), PARENT_MESSAGE_ID)
```

#### Sending a Reply Message

```kotlin
// ICON_URL, PARENT_MESSAGE_ID are optional
conversation.sendMessage(ReplyMessage(text, payload, ICON_URL), PARENT_MESSAGE_ID)
```

#### Sending an Attachment Message

An attachment message includes a file and other data.

```kotlin
// CAPTION, PARENT_MESSAGE_ID are optional
conversation.sendMessage(AttachmentMessage(file, CAPTION), PARENT_MESSAGE_ID)
```

### Fetching Older Messages

To fetch older messages from a conversation, use the `getMessages` method, which
returns a `PageIterator<Message>`. This iterator can be used to cycle through
the messages.

```kotlin
// The default page size is 10
val messageIterator = conversation.getMessages(pageSize = 10)
val firstPageOfMessages = messageIterator.items
if (messageIterator.hasNext()) {
    val nextMessagePage = messageIterator.next()
    val nextPageOfMessages = nextMessagePage.items
}
```

### Receiving and Checking Delivery of Messages

To receive the latest messages and check whether a message has been delivered to
the server, you can add a listener to the conversation or observe the Flow of
messages.

#### Receiving Messages via a Listener

Here's how you can add a listener to receive the latest messages:

```kotlin
// Using a listener
val messageArrivedListener = MessageArrivedListener { message ->
    // You can now use the received message
}
conversation.addMessageArrivedListener(messageArrivedListener)
```

Similarly, you can add a listener to check whether a message has been delivered
to the server:

```kotlin
val messageDeliveredListener = MessageDeliveredListener { message ->
    // You can now use the delivered message
}
conversation.addMessageDeliveredListener(messageDeliveredListener)
```

#### Receiving Messages via a Flow

Here's how you can use the Flow to receive the latest messages:

```kotlin
conversation.messageArrivedFlow.collect { message ->
    // You can now use the received message
}
```

Similarly, you can use the Flow to check whether a message has been delivered to
the server:

```kotlin
conversation.messageDeliveredFlow.collect { message ->
    // You can now use the delivered message
}
```

### Monitoring Participant Changes

You can monitor changes to the participants in a conversation by adding a
listener to the conversation or by observing the flow.

```kotlin
// Using a listener
conversation.addParticipantsChangedListener { changedParticipants ->
    // You can now use the set of changed participants
}
```

or

```kotlin
// Using flow
conversation.participantsFlow.collect { changedParticipants ->
    // You can now use the set of changed participants
}
```

To access participants that are only related to messaging, you can get them by

```kotlin
val messagingParticipants = conversation.participants(AxpChannel.MESSAGING)
```

# Package com.avaya.axp.client.sdk.messaging

[omni-integration]: https://documentation.avaya.com/bundle/ExperiencePlatform_Administering_10/page/Creating_an_Omni_SDK_integration.html
[gh-token]: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic
[package]: https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150732
[core-package]: https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150727
