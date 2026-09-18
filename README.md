<p align="center">
  <img src="aleph-app-icon-intro.svg" alt="Aleph" width="96">
</p>

# Aleph

Aleph is a fork of the [official Mastodon Android app](https://github.com/mastodon/mastodon-android) that adds a few features the official app is missing.

## Introduction

Aleph tracks the upstream app and adds:

- UnifiedPush support, so push notifications work without Google Play Services.
- Post content type selection (plain text, Markdown, HTML) in the compose screen, with a per-account default, when the server advertises support for it ([glitch-soc](https://github.com/glitch-soc/mastodon)).
- A bookmark button in the post actions, with boost, favorite and bookmark each taking their own color when active.
- Delete and redraft in the post menu, deleting a post and reopening its text, content warning, media, poll and quote in the compose screen, ready to be posted again.
- The content warning of a post is carried over when replying to it, whoever wrote it, like the web app does.

Everything else behaves like the official app.

Get the APK from the [Releases section](https://code.zeptotech.net/Aleph/mastodon-android/releases), or build it yourself. An F-Droid release is coming soon.

⚠️ Some Aleph features are written with the help of generative AI (under human review).

## Building

You can either import the project into Android Studio and build it from there, or run the following command in the project directory:

```shell
./gradlew assembleRelease
```

### Toolchain

Releases are built with an exact toolchain, so that the `release` APK can be reproduced from source:

| Component               | Version                         |
|-------------------------|---------------------------------|
| JDK                     | 21 (Temurin)                    |
| Gradle                  | 8.13 (wrapper, checksum-pinned) |
| Android Gradle Plugin   | 8.13.2                          |
| Android SDK platform    | 37.0 (`compileSdk`)             |
| Android SDK build-tools | 35.0.0                          |
| NDK                     | not used                        |

The build reads no other environment than the signing variables below, and `local.properties` only needs `sdk.dir`.

### Signing

Release builds are signed only when the keystore is passed through the environment. Without these variables, `assembleRelease` and `assembleGithubRelease` produce unsigned APKs.

| Variable            | Meaning                                   |
|---------------------|-------------------------------------------|
| `KEYSTORE_FILE`     | Path to the keystore file                 |
| `KEYSTORE_PASSWORD` | Store password, also used as key password |
| `KEY_ALIAS`         | Key alias, defaults to `key0`             |

Creating a release keystore:

```shell
keytool -genkeypair -v -keystore aleph-release.jks -alias key0 \
	-keyalg RSA -keysize 4096 -validity 10000
```

## Contributing

Issues and pull requests about Aleph features are welcome here. Anything that belongs to the official app should go [upstream](https://github.com/mastodon/mastodon-android) instead.

Translations are still handled upstream through [Crowdin](https://crowdin.com/project/mastodon-for-android). Aleph-only strings live in `values/aleph_strings.xml`; do not modify the upstream `strings.xml` files.

## License

This project is released under the [GPL-3 License](./LICENSE).

The Mastodon name and logo are trademarks. Aleph is an unofficial fork with its own name and icon, and is not affiliated with, endorsed by, or connected to the Mastodon non-profit organisation.
