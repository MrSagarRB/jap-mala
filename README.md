# जप माला (Jap Mala)

A mantra-chanting counter for Android. A 108-bead mala you tap to count, 12 ready mantras (plus your own), daily totals and a day streak. Offline, no login, no ads; the only permission is vibration.

**Download:** https://mrsagarrb.github.io/jap-mala/

## Build

Native Android, Kotlin + Jetpack Compose. Needs JDK 17+ and the Android SDK (API 36).

```sh
./gradlew assembleDebug      # app/build/outputs/apk/debug/
./gradlew assembleRelease    # needs keystore.properties, see below
```

### Release signing

`keystore.properties` and `*.jks` are git-ignored. To produce a signed release APK, create `keystore.properties` in the repo root:

```properties
storeFile=/absolute/path/to/your.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Without it, `assembleRelease` produces an unsigned APK.

## Landing page

`docs/` is the GitHub Pages site (`index.html`, screenshots, and the APK that the download button serves). To publish a new version, copy the signed APK to `docs/`, update the file name, size and SHA-256 in `docs/index.html`, and push.

## Licenses

Font: Noto Serif Devanagari, SIL Open Font License 1.1 (`app/src/main/assets/licenses/`). The Om glyph in the icon and splash is derived from the same font.
