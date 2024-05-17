## Introduction

Welcome to the Avaya Messaging UI SDK documentation. This SDK seamlessly integrates with the Avaya Messaging SDK, providing a highly customizable user interface for messaging screens while simplifying the integration of messaging operations into client applications. The Avaya Messaging UI SDK empowers developers to finely tailor the messaging interface, allowing customization of colors, strings, and typography to meet the specific visual requirements of your application.

## Features

1. **Conversation Details**
   Effortlessly display the business image and name of the organization at the top of the messaging interface, establishing a personalized and recognizable user experience.

2. **Message Display**
   Present messages from various sources such as agents, supervisors, and bots with clear timestamps and intuitive status indicators, including real-time updates for messages being sent, successfully delivered, or requiring a retry due to failure.

3. **Message Separation**
   Organize messages based on the date, providing users with a structured and easily navigable conversation timeline for a seamless and enjoyable messaging experience.

4. **Participant Name Display**
   Enhance conversation clarity by displaying participant names. This is particularly useful for non-customers, as it makes it easy for users to identify and follow the discussion flow. Also, show active participants in the flow at any point in the conversation.

5. **Message History**
   Retrieve and display historical messages from previous conversations, enabling users to reference past interactions for context and continuity.

6. **Infinite Scrolling**
   Improve user experience by automatically fetching older messages as users scroll to the top, ensuring a seamless and uninterrupted conversation exploration.

7. **Text Messaging**
   Enable users to effortlessly type and send text messages, providing a fundamental and user-friendly communication feature.

8. **Message Retry**
   Facilitate communication resilience by allowing users to retry sending messages that may have failed in previous attempts.

9. **Attachments**
   Enhance communication capabilities by enabling users to send attachments, either independently or with accompanying text messages, fostering a richer and more interactive messaging experience.

10. **Live Message Display**
    Provide users with a real-time display of messages exchanged between customers and agents/bots, ensuring an up-to-the-moment view of the ongoing conversation.

11. **Location Sharing**
    Empower users to share location information seamlessly when requested by an agent, enhancing the contextual richness of conversations.

12. **Rich Media and Attachments**
    Support a variety of rich media types, including postbacks, replies, and carousel displays, allowing for dynamic and engaging content within the messaging interface.

## Customization Options in Avaya Messaging UI SDK

The Avaya Messaging UI SDK empowers developers with robust customization features, allowing seamless integration of the UI with the branding and design aesthetics of their applications. To tailor the UI to your specific preferences, the SDK introduces the concept of MessagingThemeConfig, an object that plays a pivotal role in configuring the UI's visual elements.

### Using `MessagingThemeConfig`

To initiate customization, create an instance of `MessagingThemeConfig` and pass it to the UI SDK. This object acts as a blueprint for designing the UI according to your specifications. The `MessagingThemeConfig` encompasses four key parameters:

- **Light Colors and Dark Colors:** `CustomColors` - These parameters enable you to define a spectrum of colors for various UI elements, such as text, background, messaging windows, text field color, and button colors for carousel, reply, location, and postbacks. Customize light and dark color schemes to ensure a visually harmonious experience.
- **Typography:** `TextStyle` - Set the typography for all text components within the messaging screen. By defining the typography, you seamlessly integrate your chosen font styles into the UI, providing a consistent and branded textual experience.
- **Custom Icons Colors:** `CustomIconsAndColors` - Tailor the colors of icons used in the messaging screen, such as the send message icon, attach file icon, and pick image icon. The flexibility extends to setting distinct colors for both light and dark themes, ensuring a cohesive and aesthetically pleasing UI.
- **Custom Strings:** `CustomStrings` - Pass custom strings into the UI SDK, allowing you to modify text content according to your preferences. This feature supports localized strings, enabling a personalized and language-specific user experience.

### Other Customizable Elements in UI SDK

- **Flags:** Utilize the flag variables to set flags that indicate whether an agent or automation is joining or leaving the conversation. Additionally, you can choose to reveal the actual name of the agent to the customer, which enhances transparency in communication. You also have the option to use 'business' as a participant. If you choose this option, instead of displaying the agent's name and picture, the business name and logo will be shown.

- **Theme:** Manually set the theme of the UI SDK using the `setUiThemeLight()` and `setUiThemeDark()` functions. Once set, the theme remains unchanged until modified. In the absence of a specified theme, the UI SDK defaults to the system theme.

The Avaya Messaging UI SDK's customization capabilities empower developers to create a tailored, visually cohesive, and brand-aligned user interface. By leveraging the various parameters within `MessagingThemeConfig`, developers can ensure a seamless integration that reflects the unique identity of their applications.

### Using the Messaging UI SDK in Your Application

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
manually from [its package page][package].

You'll also need to download the [Core module][core-package]
and [Messaging Module][messaging-package] that it depends on.

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

## Getting Started with UI Customization and flags

## AvayaMessagingUiSdk

### Flags:

- `showAgentEvents` :
  Set this flag to true if you want to show the agent joined and left events in the messaging window.
- `showAutomationEvents` :
  Set this flag to true if you want to show the automation joined and left events in the messaging window.
- `showAgentName` :
  Set this flag to true if you want to show the agent name in the messaging window.
- `showActiveParticipants` :
  Set this flag to true if you want to show the active participants list in the messaging window.
- `useBusinessAsParticipant` :
  Set this flag to true if you want to use business as a participant in the messaging window.

### Functions:

- `setUiThemeLight()`
  Set the UI theme to light.
- `setUiThemeDark()`
  Set the UI theme to dark.
- `setMessagingTheme(theme: MessagingThemeConfig)`
  Customize the messaging theme configuration.
- `getConversationHandler(conversation: Conversation)`
  Retrieve the conversation handler object by passing the conversation object from the Messaging SDK. This object manages all conversation-related functionalities. You should pass it when calling the composable function for the UI.
- `setUiEventHandler(handler: UiEventHandler)`
  This handler provides functionality that should be handled by the app, like fetching location details and providing a location URL using latitude and longitude.
- `setInternetState(connected: Boolean)`
  If your app is observing the internet connection state, you can call this function to provide updates for the internet connection state. It will be reflected on the messaging window.
- `setLanguage(locale: Locale, customStrings: CustomStrings?)`
  You can use this function to change the language of the messaging window. For supported languages, the SDK handles localized strings, however, it can still be updated using customStrings. For unsupported languages, default English strings will be used if not provided in the custom strings passed.
- `supportedLocales()` This function returns a set of locales that are supported by default. This means it has localized strings for these locales by default.
- `isSupportedLocale(locale: Locale)` This function returns `true` if the provided locale is supported by default, otherwise it returns `false`.

### ConversationHandler

The `ConversationHandler` exposes essential properties and methods for developers:

- `pageSize`: Set and get the pagination size for retrieving older messages.
- `hasNewEvent`: Live data to observe if there are any new events.
- `newEventRead()`: Used to reset the hasNewEvent flag.

### UiEventHandler

`UiEventHandler` provides functionality that should be handled by the app. It includes the following methods:

- `getLocationDetails(onComplete:(LocationMessage?)->Unit)`: The app should fetch the location and call the `onComplete` method with the location details. If it fails to fetch the location details, it should call the `onComplete` method with `null`.
- `getLocationUrl(latitude:Double?,longitude:Double?):String` : The app should provide the location URL using latitude and longitude. When clicked, the user will be directed to maps or a browser.

### Show Messaging UI

- To show the messaging UI, you can use the `ShowMessagingUI(conversationHandler: ConversationHandler)` composable function. You can pass the conversation handler object to this function and the UI for that conversation will be rendered on the Messaging screen.  

[gh-token]: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic
[package]: https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150733
[core-package]: https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150727
[messaging-package]: https://github.com/AvayaExperiencePlatform/omni-sdk-android/packages/2150732
