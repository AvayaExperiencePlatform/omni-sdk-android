# Sample Calling App

## Overview

This is the AXP Client SDK sample calling application for Android. It
demonstrates how to establish a voice call to an AXP agent queue with a very
simple user interface.

The app uses Google's [Jetpack Telecom API]
(https://developer.android.com/develop/connectivity/telecom/voip-app/telecom)
for managing the audio devices and avoiding collisions with other calling
applications on the device. It is largely based on the [Telecom sample app]
(https://github.com/android/platform-samples/tree/main/samples/connectivity/telecom)
and uses the same structure for communicating the telecom call state to the app.

On a call, the user can toggle audio mute on and off, enter DTMF digits to be
sent, switch between the audio devices available on the Android machine, as well
as hang up when the call is done.

## App Configuration

The sample calling app is written to query an external web server to get its
configuration, using the same endpoint as is queried for the JWT token.

When first run, the user must enter the settings screen (by tapping the gear
icon in the top right of the starting screen), and provide the following values:

* **Server URL** is the full URL to the application web server for configuration.
* **User ID** is the user ID passed to AXP. This will typically be an email
  address or phone number.
* **User Name** is the user's display name.
* **Verified Customer** indicates if the end-customer has been verified and the
  customer name and identifiers can be trusted by the Contact Center.

After the user presses the Save button, a request is sent to the configuration
server.

The expected configuration received from the server is:
* **hostname** is the hostname of the AXP REST API endpoint to contact
* **integrationId** is the integration ID configured for your application in AXP
* **appKey** is your application's key for using API Exchange Hub
* **remoteAddress** the phone number on AXP to be called

The first three values are passed into the AXP Client SDK at configuration time,
and are further described in the documentation for the Core module. The remote
address is used when the user presses the Call icon to start the call.

All of this configuration will be needed in a real calling app, but you can
provide it however you wish in some combination of hard-coded data, data
entered by the end user, and data queried from a remote server.

## App Structure

The sample calling app is a modern Android app using Jetpack Compose for the UI,
Kotlin Flows for observing data in the model layer, and storing settings using
the datastore API.

When the user presses the Call button, the `CallActivity` is started. It
monitors the Jetpack Telecom state via the `TelecomRepository` and when it
detects that there is currently an active call, creates a `TelecomCallScreen` to
display the call.

For an active call, the `TelecomCall.Registered` object wraps the AXP type `Call`
for the call. That interface provides flows for monitoring the state of call
and provides the methods for sending DTMF digits and hanging up the call.

On the call screen, the Composable `CallControls` provides a row of buttons at
the bottom of the screen for the user to control the call. Each of those buttons
is hooked up to the corresponding part of the `Call` object for displaying the
call state as well as performing any needed actions.
