# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

This is **Aleph** (`com.madeorsk.aleph`), a fork of the official Mastodon Android app that adds support for features from Mastodon forks such as glitch-soc (post content types). Keep fork-specific code separated from upstream code so upstream merges stay cheap.

## Build & test

JDK 17+ required (Java 17 language features). Single Gradle module: `:mastodon`.

```shell
./gradlew assembleDebug                 # debug APK
./gradlew assembleRelease               # minified release
./gradlew test                          # JVM unit tests (mastodon/src/test)
./gradlew testDebugUnitTest --tests 'org.joinmastodon.android.NumberAbbreviationTests'   # single test class
./gradlew connectedScreenshotsUiTestAndroidTest   # instrumented screenshot generator, needs a device
```

`local.properties` must contain `sdk.dir`. Unit tests run with `unitTests.returnDefaultValues=true`, so Android framework calls return defaults rather than throwing.

Build types: `debug`, `release`, `beta` (release + `-beta` suffix), `githubDebug`/`githubRelease` (source root switched to `src/github`, adds the in-app self-updater), `screenshotsUiTest` (non-debuggable debug used as `testBuildType`; select it in the Build Variants panel if Android Studio says "module not specified").

Fastlane lanes `test`, `beta`, `deploy` drive CI; releases and Play uploads happen there, not locally.

## Versioning

`versionName` is `<upstream version>+aleph-<aleph version>` (e.g. `2.13.3+aleph-1.0.0`): the upstream release the fork is rebased on, then Aleph's own semver. Bump the upstream part when merging upstream, the Aleph part for Aleph-only releases.

`versionCode` is `<upstream versionCode> * 100 + <aleph release index>`, the index starting at 0 for the first Aleph release on that upstream base (upstream 190 gives 19000, then 19001 for an Aleph-only release, and an upstream bump to 191 gives 19100). This keeps codes ordered whether the release comes from upstream or from Aleph alone, with room for 100 Aleph-only releases per upstream version. Release tags are `v<versionName>`.

Changelogs live in `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`, with `default.txt` symlinked to the latest one. `./updateChangelog.sh` creates the file for the current `versionCode` and moves the symlink.

## Architecture

Java-only Android app, no DI framework, no Kotlin, no AndroidX fragments/appcompat.

- **appkit** (`me.grishka.appkit`) is the app framework. `MainActivity extends FragmentStackActivity`: one activity, a stack of **platform** `android.app.Fragment`s. Navigate with `Nav.go(activity, SomeFragment.class, args)`; pass models through args with Parceler (`Parcels.wrap`). AndroidX-equivalent widgets come from **litex** (`me.grishka.litex:*`), repackaged slices of AndroidX.
- **API layer** (`api/`): every endpoint is a class extending `MastodonAPIRequest<T>` (or `HeaderPaginationRequest`, `ResultlessMastodonAPIRequest`), grouped by endpoint family under `api/requests/`. Run them with `.exec(accountID)` or `.execNoAuth(domain)` plus an appkit `Callback`; `MastodonAPIController` owns the OkHttp client and the shared Gson instance.
- **Models** (`model/`) are plain public-field classes extending `BaseModel`, deserialized by Gson with `LOWER_CASE_WITH_UNDERSCORES` naming, so `spoilerText` maps to `spoiler_text`. Mark fields `@RequiredField` (or the class `@AllFieldsAreRequired`) to have `postprocess()` reject null; override `postprocess()` for derived state (e.g. parsing HTML). Add `@Parcel` when the model travels in fragment args.
- **Sessions**: `AccountSessionManager` is the singleton holding all logged-in `AccountSession`s, instance info, custom emoji, and filters. Almost everything is keyed by an `accountID` string that fragments carry in their args. Per-account state lives in `AccountLocalPreferences`; device-wide toggles are static fields on `GlobalUserPreferences` (`load()`/`save()`).
- **Caching**: each session has a `CacheController` backed by its own SQLite database (home timeline, notifications, lists, recent searches) with a stale-while-revalidate `getX(..., forceReload, callback)` shape. All DB work runs on `CacheController.databaseThread`.
- **Timelines and the display-item system** (`ui/displayitems/`): a post is not one view holder. `StatusDisplayItem.buildItems(...)` flattens a `Status` into a list of items (header, text, spoiler, media grid, poll options, link card, footer, quote, ...), each with its own view holder; `BaseStatusListFragment` renders that flat list and implements `StatusDisplayItem.Callbacks`. New post UI usually means a new display item type plus its `Type` enum entry and `createViewHolder` case, not a change to one big holder. `FLAG_*` constants on `StatusDisplayItem` control which items get built (quote, no footer, full width, ...).
- **Cross-screen updates** go through the Otto async bus wrapped in `E` (`E.post`, `E.register`/`unregister` in `onCreate`/`onDestroy`) with one small event class per change in `events/`. Optimistic interaction state (fav/boost/bookmark) is applied by `StatusInteractionController` and broadcast as `StatusCountersUpdatedEvent`.
- **Push notifications** require Play Services (or microG): `PushSubscriptionManager` registers over the raw `com.google.android.c2dm.intent.REGISTER` intent aimed at `com.google.android.gms` (no Firebase SDK), then subscribes the resulting `fcm.googleapis.com` endpoint on the server; `PushNotificationReceiver` gets `c2dm.intent.RECEIVE` and decrypts the Web Push payload on device. There is no UnifiedPush and no polling fallback. In-app notifications (the Notifications tab) are plain REST and work without Google. `googleservices/` is the hand-rolled GMS client, used only by the barcode scanner.

## Conventions

- Code style follows the existing files: tabs for indentation, no space before `{` or after `if`/`for`, no spaces around operators in conditions (`if(maxID!=null)`).
- **Strings**: all Aleph strings go in `values/aleph_strings.xml` (translations in `values-*/aleph_strings.xml`). Never add or edit entries in `values/strings.xml` or any `values-*/strings.xml`: those are upstream's, owned by Crowdin (`crowdin.yml`, sync workflows), and edits there conflict on every upstream sync. If an upstream string needs different wording, ask first. `generateLocaleConfig` is on, so no manual locale list.
- Never edit files through Bash (python heredocs, `sed`, `awk`). Use the Read/Edit/Write tools; Bash is for builds, tests, git and search.
- Upstream UI changes go through the Mastodon core-team design process, so keep Aleph's own UI additions self-contained and easy to rebase.
