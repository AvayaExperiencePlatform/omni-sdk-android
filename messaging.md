# AXP-Messaging

Welcome to the AXP Messaging module! This powerful tool is part of the Avaya Experience Platform™ Digital, a software platform designed to enhance business communication. 

The AXP Messaging module enables asynchronous communication, allowing end users to resume conversation threads at any time and view all previous messages exchanged as part of the conversation. This is unlike a session-based chat, where the chat is closed after the participants disconnect the dialog.

This document will guide you through the installation and usage of the AXP Messaging module. Let's get started!

### :warning: Disclaimer

    Installing, downloading, copying or using this SDK is subject to terms and conditions available in the LICENSE file
## Installation

Before you can start using the Messaging SDK, there are a few setup steps you need to complete.

### Provisioning Requirements

First, ensure that the messaging integration provisioning requirements are met. This involves providing the `integrationId`, a valid JWT Token Provider, and an Application Key `appkey` during the [initialization process](https://developers.avayacloud.com/avaya-experience-platform/docs/digital-channel-javascript-sdk-overview#initialization). 

For detailed instructions on how to provision the messaging integration, obtain the `integrationId` and `appkey`, and generate a valid JWT Token, please refer to [this guide](https://developers.avayacloud.com/avaya-experience-platform/docs/digital-channel-chat-sdk-provisioning).

### Adding the AXP Messaging Module to Your Project

The AXP Messaging module is distributed as a Maven artifact. To include it in your project, you need to add the following lines to your `build.gradle` file:

If you're using Groovy, add:

```groovy
dependencies {
    implementation 'com.avaya.sdk:messaging:${avayaSdkVersion}'
}
```

If you're using Kotlin DSL, add:

```kotlin
dependencies {
    implementation("com.avaya.sdk:messaging:${avayaSdkVersion}")
}
```

In both cases, replace ${avayaSdkVersion} with the latest version of the AXP SDK. You can find the latest version on the [AXP SDK releases page]().

After adding these lines, run a build to download and integrate the AXP Messaging module into your project.

With these steps, you should be ready to start using the Messaging SDK in your project. If you encounter any issues, please refer to our [troubleshooting guide]() or contact our [support team]().

## Functionality

The AXP Messaging module offers a range of features, including adding and removing idle timeout listeners, emitting idle timeouts, listening to event states, reconnecting sessions, resetting idle timeouts, and fetching notification data.

## Sending a Message with the AXP Messaging Module

The AXP Messaging module allows you to send messages to agents in AXP. This feature is built on the AXP Core module, which manages user sessions.

### Prerequisites

Before you can send a message, you need a valid `Conversation`. You can obtain this by calling `AxpClientSdk.getDefaultConversation()` from the AXP Core module.

Here's an example of starting a new user session:

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

## Sending a Message

Once you have a valid `Conversation`, you can use it to send message.

```kotlin
val message = "Hello, how can I help you?"
conversation.sendMessage(message)
```

The AXP Messaging module supports various types of messages, including:

- Text(Plain text, Emoji's, Links)
- Postback
- Reply
- Attachment
- Location

## Sending a Location Message

A location message includes latitude and longitude coordinates. Here's an example of how you might
send a location message:

```kotlin
// LOCATION_NAME, ADDRESS_LINE, PARENT_MESSAGE_ID are optional
conversation.sendMessage(LocationMessage(latitude, longitude, LOCATION_NAME, ADDRESS_LINE), PARENT_MESSAGE_ID)
```

## Sending a Postback Message

Here's an example of how you might send a postback message:

```kotlin
// PARENT_MESSAGE_ID is optional
conversation.sendMessage(PostbackMessage(text, payload), PARENT_MESSAGE_ID)
```

## Sending a Reply Message

Here's an example of how you might send a reply message:

```kotlin
// ICON_URL, PARENT_MESSAGE_ID are optional
conversation.sendMessage(ReplyMessage(text, payload, ICON_URL), PARENT_MESSAGE_ID)
```

## Sending an Attachment Message

An attachment message includes a file and other data. Here's an example of how you might send an
attachment message:

```kotlin
// CAPTION, PARENT_MESSAGE_ID are optional
conversation.sendMessage(AttachmentMessage(file, CAPTION), PARENT_MESSAGE_ID)
```

for all the above methods, we are even supporting the callback to get the status of the message
sent.

## Fetching Older Messages

The AXP Messaging module allows you to fetch older messages from a conversation. This is done using the `getMessages` method, which returns a `PageIterator<Message>`. This iterator can be used to cycle through the messages.

Here's an example of how to fetch older messages:

```kotlin
// The default page size is 10
val messageIterator = conversation.getMessages(pageSize = 10)
if (messageIterator.hasNext()) {
    val nextMessagePage = messageIterator.next()
    // You can now use the messages from nextMessagePage
}
```

## Receive Latest Messages

You can receive latest messages by adding a listener to the conversation or listening to the flow

```kotlin
// Using a listener
conversation.addMessageArrivedListener { message ->
    // You can now use the received message
}

// Using flow
conversation.messageArrivedFlow.collect { message ->
    // You can now use the received message
}

To check whether message has been delivered to the server. You can listen by adding a listener to
the conversation or listening to the flow

```kotlin
// Using a listener
conversation.addMessageDeliveredListener { message ->
    // You can now use the delivered message
}

// Using flow
conversation.messageDeliveredFlow.collect { message ->
    // You can now use the delivered message
}
```

## Monitoring Participant Changes

You can monitor changes to the participants in a conversation by adding a listener to the conversation or by listening to the flow. Here's how:


```kotlin
// Using a listener
conversation.addParticipantsChangedListener { participantChange ->
    // You can now use the participantChange object
}

// Using flow
conversation.participantsFlow.collect { participants ->
    // You can now use the participants object
    val newParticipants: Set<Participant> = participants
}
```

To access participants that are only related to messaging, you can get them by

```kotlin
val messagingParticipants = conversation.participants(AxpChannel.MESSAGING)
```

