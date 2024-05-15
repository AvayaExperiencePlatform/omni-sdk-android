# Module AXP Calling

This is the module for voice calling using WebRTC in the Android version of the
AXP Client SDK.

It provides the logic for engaging in a voice call with an agent in AXP, and
depends on the Core module for configuring the SDK and authenticating with AXP.

## Prerequisites

To use the Calling module, you need an Omni SDK integration provisioned with
the **WebRTC Voice** service enabled. Follow the instructions in
[Creating an Omni SDK Integration]
(https://documentation.avaya.com/bundle/ExperiencePlatform_Administering_10/page/Creating_an_Omni_SDK_integration.html)
to set up an integration with voice support and use the integration ID for
configuring the SDK as described in the documentation for the Core module.

## How to Start a Call

1. **Configure the SDK**

   You must first configure the SDK as described in the documentation for the
   Core module.

2. **Get the Conversation**

   A `Conversation` represents an ongoing dialog with an agent or agents in AXP,
   typically for a single topic.

   If you don't already have a reference to the current conversation, you can
   get it via `AxpClientSdk.getDefaultConversation()`. This will implicitly
   create a new one if needed.

3. **Add a Call to the Conversation**

   Once you have a `Conversation`, call the extension method `addCall` on it to
   start a call to AXP.

   The call will be routed to an agent as per normal for your tenant, based on
   the value of the `address` parameter. The address value should be a phone
   number configured on AXP to route to your desired agent queue for the call.

   The initial media state is controlled by the optional parameter
   `mediaSettings`, which has two options:

    * `startWithAudioMuted` controls whether the call begins with the local audio
      muted, defaulting to having the audio not muted

    * `forceRelay` when set forces the media traffic to use a TURN relay in the
      ICE negotiation for the media path (see RFCs 5766 and 8445 for details).
      This defaults to off as it should only be needed in uncommon network
      conditions.

   The optional parameter `engagementParameters` is a map of string key/value
   pairs passed to AXP that will be used in routing the call and initial
   processing as configured for your tenant.

   The final optional parameter is `isPriority` which indicates to AXP if the
   call should be treated as a priority call. It defaults to not being priority.

   The returned `Call` object contains properties for observing the state
   of the call and methods for altering the state of the call. See the sample
   calling app for example uses of them.

## Quick Start

If you are only creating calls and do not need integration with the Messaging
module, there is a convenience extension method that combines steps 2 and 3.
Simply call the `AxpCalling.startCall` method, which takes the same parameters
as `addCall`.

## Foreground Service

To prevent Android from killing an active call when your app is placed in the
background, the AXP SDK starts a foreground service for the call.

To support this in your app, you need to do two things:

1. **Add the Service to AndroidManifest.xml**

   In your application's `AndroidManifest.xml` file, add the following:

```xml
        <service
            android:name="com.avaya.axp.client.sdk.webrtc.TelecomCallService"
            android:foregroundServiceType="phoneCall"
            android:exported="false" />
```

2. **Update the notification on telecom call state changes**

   The SDK provides the interface `CallNotificationManager` to notify the app
   when the notification for the call service needs to be updated. Register your
   implementation of it in the global variable
   `com.avaya.axp.client.sdk.webrtc.callNotificationManager`

   The implementation should update a notification channel based on the current
   state of the call, or remove the notification if there is no active call. See
   the sample calling app for an example of this.

## Jetpack Telecom Integration

The AXP Calling SDK has integration with Google's [Jetpack Telecom API]
(https://developer.android.com/develop/connectivity/telecom/voip-app/telecom)
to simplify writing calling applications.

The SDK already implements everything needed for calling with Jetpack Telecom.
To use it, in your application, instead of starting calls manually as described
above by getting a `Conversation` and calling `addCall` on it, instead call the
utility extension method `Context.launchOutgoingCall`. This sends an `Intent`
to trigger the call service, which calls into the AXP SDK APIs to start the call
on the default conversation. It takes the same parameters as `addCall`.

# Package com.avaya.axp.client.sdk.webrtc
