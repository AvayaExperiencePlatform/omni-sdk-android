## Introduction

Welcome to the Avaya Messaging UI SDK documentation. This SDK seamlessly integrates with the Avaya Messaging SDK, providing a highly customizable user interface for chat screens while simplifying the integration of messaging operations into client applications. The Avaya Messaging UI SDK empowers developers to finely tailor the chat interface, allowing customization of colors, strings, and typography to meet the specific visual requirements of your application.

## Features

1. **Conversation Details**
   Effortlessly display the Business image and name of the organization at the top of the messaging interface, establishing a personalized and recognizable user experience.

2. **Message Display**
   Present messages from various sources such as agent, supervisor  and bot with clear timestamps and intuitive status indicators, including real-time updates for messages being sent, successfully delivered, or requiring a retry due to failure.

3. **Message Separation**
   Organize messages based on the date, providing users with a structured and easily navigable conversation timeline for a seamless and enjoyable messaging experience.

4. **Participant Name Display**
   Enhance conversation clarity by displaying participant names, particularly useful for non-customers, making it easy for users to identify and follow the discussion flow. And show active participants in the flow at any point of conversation.

5. **Message History**
   Retrieve and display historical messages from previous conversations, enabling users to reference past interactions for context and continuity.

6. **Infinite Scrolling**
   Improve user experience by automatically fetching older messages as users scroll to top, ensuring a seamless and uninterrupted conversation exploration.

7. **Text Messaging**
   Enable users to effortlessly type and send text messages, providing a fundamental and user-friendly communication feature.

8. **Message Retry**
   Facilitate communication resilience by allowing users to retry sending messages that may have failed in previous attempts.

9. **Attachments**
   Enhance communication capabilities by enabling users to send attachments, either independently or with accompanying text messages, fostering a richer and more interactive messaging experience.

10. **Live Message Display**
    Provide users with a real-time display of messages exchanged between customer and agent/bot, ensuring an up-to-the-moment view of the ongoing conversation.

11. **Location Sharing**
    Empower users to share location information seamlessly when requested by an agent, enhancing the contextual richness of conversations.

12. **Rich Media and Attachments**
    Support a variety of rich media types, including postbacks, replies, and carousel displays, allowing for dynamic and engaging content within the messaging interface.

## Customization Options in Avaya Messaging UI SDK

The Avaya Messaging UI SDK empowers developers with robust customization features, allowing seamless integration of the UI with the branding and design aesthetics of their applications. To tailor the UI to your specific preferences, the SDK introduces the concept of MessagingThemeConfig, an object that plays a pivotal role in configuring the UI's visual elements.

### Using `MessagingThemeConfig`

To initiate customization, create an instance of `MessagingThemeConfig` and pass it to the UI SDK. This object acts as a blueprint for designing the UI according to your specifications. The `MessagingThemeConfig` encompasses four key parameters:

- **Light Colors and Dark Colors:** `CustomColors` - These parameters enable you to define a spectrum of colors for various UI elements, such as text, background, chat windows, text field color, and button colors for carousel, reply, location, and postbacks. Customize light and dark color schemes to ensure a visually harmonious experience.
  
- **Typography:** `TextStyle` - Set the typography for all text components within the chat screen. By defining the typography, you seamlessly integrate your chosen font styles into the UI, providing a consistent and branded textual experience.
  
- **Custom Icons Colors:** `CustomIconsAndColors` - Tailor the colors of icons used in the chat screen, such as the send message icon, attach file icon, and pick image icon. The flexibility extends to setting distinct colors for both light and dark themes, ensuring a cohesive and aesthetically pleasing UI.
  
- **Custom Strings:** `CustomStrings` - Pass custom strings into the UI SDK, allowing you to modify text content according to your preferences. This feature supports localized strings, enabling a personalized and language-specific user experience.

### Other Customizable Elements in UI SDK

- **Flags:** Utilize the flag variables to set flags indicating whether to display agent and automation joining and leaving the conversation. Additionally, choose to reveal the actual name of the agent to the customer, enhancing transparency in communication. You can also use business as participant.

- **Theme:** Manually set the theme of the UI SDK using the `setUiThemeLight()` and `setUiThemeDark()` functions. Once set, the theme remains unchanged until modified. In the absence of a specified theme, the UI SDK defaults to the system theme.

The Avaya Messaging UI SDK's customization capabilities empower developers to create a tailored, visually cohesive, and brand-aligned user interface. By leveraging the various parameters within `MessagingThemeConfig`, developers can ensure a seamless integration that reflects the unique identity of their applications.

### Using the Messaging UI SDK in Your Application

## Integration Steps

To incorporate the Messaging UI SDK into your application, follow these simple steps:

1. **Download the SDK:**
   Add the SDK to any folder within your application. Obtain the absolute path of the `messaging_android_ui_sdk.aar` file.

2. **Update `build.gradle`:**
   Open your app-level `build.gradle` file.
   Add the following dependency to include the Messaging UI SDK:

   ```groovy
   dependencies {
       implementation files('{path}/messaging_android_ui_sdk.aar')
   }
   ```
Replace `{path}` with the absolute path of the `messaging_android_ui_sdk.aar` file. And sync the project.

## Getting Started with UI Customization and flags

## AvayaMessagingUiSdk

### Flags:
- `showAgentEvents` :
Set this flag to true if you want to show the agent joined and left events in the messaging window.
- `showAutomationEvents` :
Set this flag to true if you want to show the automation joined and left events in the messaging window
- `showAgentName` :
Set this flag to true if you want to show the agent name in the messaging window
- `showActiveParticipants` :
Set this flag to true if you want to show the active participants list in the messaging window
- `useBusinessAsParticipant` :
Set this flag to true if you want to use business as participant in the messaging window

### Functions:

- `setUiThemeLight()`
Set the UI theme to light.
- `setUiThemeDark()`
Set the UI theme to dark.
- `setMessagingTheme(theme: MessagingThemeConfig)`
Customize the messaging theme configuration.
- `getConversationHandler(conversation: Conversation)`
Retrieve the conversation handler object by passing the conversation object from the messaging SDK. This object manages all conversation-related functionalities. You should Pass it while calling the composable function for ui
- `setUiEventHandler(handler: UiEventHandler)`
This handler provides functionality that should be handled by app like fetching location details and providing location url using latitude and longitude
- `setInternetState(connected: Boolean)`
If your app is observing internet connection state, you can call this function to provide updates for internet connection state. It will be reflected on messaging window
- `setLanguage(locale: Locale, customStrings: CustomStrings?)`
You can use this function to change the language of the messaging window. For supported languages sdk handles localized strings, however it can still be updated using customStrings. For unsupported languages, default English strings will be used if not provided in custom strings passed.
- `supportedLocales()`
This function returns set of locales supported by default which means it has localized strings for these locales by default.
- `isSupportedLocale(locale: Locale)`
This function returns true if provided locale is supported by default else returns false

### ConversationHandler

The `ConversationHandler` exposes essential properties and methods for developers:

- `pageSize`: Set and get the pagination size for retrieving older messages.
- `hasNewEvent`: Live data to observe if there are any new events.
- `newEventRead()`: Used to reset hasNewEvent flag.

### UiEventHandler
- `UiEventHandler` provides functionality that should be handled by the app. It includes the following methods:
- `getLocationDetails(onComplete:(LocationMessage?)->Unit)`: THe app should fetch the location and call the onComplete method with the location details or null if failed to fetch the location details.
- `getLocationUrl(latitude:Double?,longitude:Double?):String` : The app should provide the location url using latitude and longitude clicking on which user will be directed to maps or browser.

### Show Messaging UI
- To show the messaging UI, you can use the `ShowMessagingUI(conversationHandler: ConversationHandler)` composable function. You can pass the conversation handler object to this function and UI for that conversation will be rendered on Messaging screen.
