# Demo Applications

Bundled **ShopMate** e-commerce apps for first-run agentic tests. Same user flow on iOS and Android.

---

## Apps

| App | Stack | Bundle / Package | Prebuilt binary |
|-----|-------|------------------|-----------------|
| **shopmate-ios** | SwiftUI | `com.mobtest.shopmate` | `shopmate-ios/dist/ShopMate.app` |
| **shopmate-android** | Jetpack Compose | `com.mobtest.shopmate` | `shopmate-android/dist/shopmate-debug.apk` |

---

## User flow

```
Welcome → Login → Home → Products → Cart → Checkout → Success
```

**Demo credentials:** `demo@shopmate.com` / `secret123`

---

## Rebuild after source changes

```bash
./scripts/build-demo-app.sh ios
./scripts/build-demo-app.sh android
```

Requires Xcode (iOS) or Android Studio / SDK (Android).

---

## Use in tests

Default paths are set in `src/test/resources/config.properties`. The agent tests live in `ShopMateDemoTest.java`.

To test **your own app** instead, see [docs/CUSTOMIZE.md](../docs/CUSTOMIZE.md).
