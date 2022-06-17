# Locstring Generator

<!-- Plugin description -->
A plugin for JetBrains IDEs, which helps extract literal JS/TS string values to a dedicated "*.locstring" file, for translation (i18n).

When the cursor is on a string constant inside a JavaScript / TypeScript file, the "**Extract string literal to locstring file**" intention action will become available.
When invoked, a balloon dialog will pop open with option to modify the new locstring entry's key, value and comment.

The nearest locstring file to the edited code file will be automatically selected.
If no locstring file was found within 3 directory levels up, a new locstring file path will be suggested.
<!-- Plugin description end -->

## Installation

  Download the [latest release](https://github.com/NitsanAmit/webstorm-locstring-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## TODO:
* Mark literal strings as warnings inside .ts, .tsx, .js, .jsx files.

---
Plugin created by @nitsanamit, based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
