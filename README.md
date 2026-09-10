<p align="center">
  <img src="aleph-app-icon-intro.svg" alt="Aleph" width="96">
</p>

# Aleph

Aleph is a fork of the [official Mastodon Android app](https://github.com/mastodon/mastodon-android) that adds a few features the official app is missing.

## Introduction

Aleph tracks the upstream app and adds:

- UnifiedPush support, so push notifications work without Google Play Services.
- Post content type selection (plain text, Markdown, HTML) in the compose screen, with a per-account default, when the server advertises support for it ([glitch-soc](https://github.com/glitch-soc/mastodon)).

Everything else behaves like the official app.

Get the APK from the [Releases section](https://github.com/Madeorsk/mastodon-android/releases/latest), or build it yourself. An F-Droid release is coming soon.

## Building

As this app is using Java 17 features, you need JDK 17 or newer to build it. Other than that, everything is pretty standard. You can either import the project into Android Studio and build it from there, or run the following command in the project directory:

```shell
./gradlew assembleRelease
```

## Contributing

Issues and pull requests about Aleph features are welcome here. Anything that belongs to the official app should go [upstream](https://github.com/mastodon/mastodon-android) instead.

Translations are still handled upstream through [Crowdin](https://crowdin.com/project/mastodon-for-android). Aleph-only strings live in `values/aleph_strings.xml`; do not modify the upstream `strings.xml` files.

## License

This project is released under the [GPL-3 License](./LICENSE).

The Mastodon name and logo are trademarks. Aleph is an unofficial fork with its own name and icon, and is not affiliated with, endorsed by, or connected to the Mastodon non-profit organisation.
