
# JnForce
## Description
<!-- Plugin description -->
This plugin generates Kotlin data classes from Salesforce metadata to simplify JSON serialization
and deserialization with Jackson. It streamlines integration with REST and Bulk APIs by providing type-safe
models. It maybe especially helpful when you have to deal with frequent changes in Salesforce Objects.
<!-- Plugin description end -->


## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "jnforce"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:
  Currently not supported

## Configuration

Go to Settings → Tools → JnForce Settings.

Enter the base URL of your Salesforce instance and your credentials. To connect, you must create a Salesforce Connected App and provide the Client ID and Client Secret.

Currently, only the Client Credentials authentication flow is supported. Other OAuth flows can be added upon request.

You also need to specify the full package name for the generated data classes. This determines both their namespace and the output directory structure.

Salesforce objects can be selected in one of two ways:
– by filtering based on object properties (e.g., creatable, retrievable), or
– by providing a comma-separated list of class names.

## Usage

1. Open a Kotlin project.
2. Click `Tools → Generate Salesforce Classes` 
3. Classes will appear in the selected package.
4. Classes should work with jackson for de/serialization. 
 
---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation