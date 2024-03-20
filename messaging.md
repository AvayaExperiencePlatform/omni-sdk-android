# AXP-Messaging

The AXP Messaging Sdk enables asynchronous communication, allowing end users to resume conversation threads at any time and view all previous messages exchanged as part of the conversation. This is unlike a session-based chat, where the chat is closed after the participants disconnect the dialog.

This document will guide you through the installation and usage of the AXP Messaging Sdk. Let's get started!

### Adding the AXP Messaging SDK to Your Project

The AXP Messaging sdk is distributed as a Maven artifact. To include it in your project, you need to add the following lines to your `build.gradle` file:

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

After adding these lines, run a build to download and integrate the AXP Messaging sdk into your project.

With these steps, you should be ready to start using the Messaging SDK in your project.

## Functionality

The AXP Messaging sdk offers a range of features, including adding and removing idle timeout listeners, emitting idle timeouts, listening to event states, reconnecting sessions, resetting idle timeouts, and fetching notification data.

## Sending a Message with the AXP Messaging sdk

The AXP Messaging sdk allows you to send messages to agents in AXP. This feature is built on the AXP Core sdk, which manages user sessions.

### Prerequisites

Before you can send a message, you need a valid `Conversation`. You can obtain this by calling `AxpClientSdk.getDefaultConversation()` from the AXP Core module.

Here's an example of getting default conversation:

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

## Receiving and Checking Delivery of Messages

To receive the latest messages and check whether a message has been delivered to the server. You can achieve this by adding listeners to the conversation or using Kotlin's Flow.

Here's how you can add a listener to receive the latest messages:


```kotlin
// Using a listener
val messageArrivedListener=MessageArrivedListener { message ->
    // You can now use the received message
}
conversation.addMessageArrivedListener(messageArrivedListener)
```

Similarly, you can add a listener to check whether a message has been delivered to the server:

```kotlin
val messageDeliveredListener=MessageDeliveredListener { message ->
    // You can now use the delivered message
}
conversation.addMessageDeliveredListener(messageDeliveredListener)
```
### Using Flow
Here's how you can use Flow to receive the latest messages:
```kotlin
conversation.messageArrivedFlow.collect { message ->
    // You can now use the received message
}
```
Similarly, you can use Flow to check whether a message has been delivered to the server:
```kotlin
conversation.messageDeliveredFlow.collect { message ->
    // You can now use the delivered message
}
```
By using listeners or Flow, you can effectively handle incoming messages and check their delivery status in your application.


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

