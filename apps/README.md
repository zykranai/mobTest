# Demo Applications

Bundled **ShopMate** e-commerce apps for first-run agentic tests.

| App | Source | Prebuilt binary |
|-----|--------|-----------------|
| **shopmate-ios** | SwiftUI | `shopmate-ios/dist/ShopMate.app` |
| **shopmate-android** | Jetpack Compose | `shopmate-android/dist/shopmate-debug.apk` |

Rebuild after source changes:

```bash
./scripts/build-demo-app.sh ios
./scripts/build-demo-app.sh android
```

Demo credentials: `demo@shopmate.com` / `secret123`
