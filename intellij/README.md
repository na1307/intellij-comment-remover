# Comment Remover for IntelliJ based IDEs

![Build](https://github.com/na1307/intellij-comment-remover/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26369.svg)](https://plugins.jetbrains.com/plugin/26369)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26369.svg)](https://plugins.jetbrains.com/plugin/26369)

<!-- Plugin description -->
This plugin removes comments from the code.

This can remove:
- Single line, Multi-line comments in most languages (Java, Kotlin, JavaScript, TypeScript, HTML, CSS, etc.)
- JavaDoc, KDoc comments

This can't remove:
- Comments in C#, VB, C++ in Rider (use an [additional plugin](https://plugins.jetbrains.com/plugin/26398) instead)
- Multi-line comments in Python, because these are actually strings

Inspired by Mads Kristensen's [Comment Remover](https://marketplace.visualstudio.com/items?itemName=MadsKristensen.CommentRemover) Visual Studio extension.
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Comment Remover"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/26369) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/26369/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/na1307/intellij-comment-remover/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
