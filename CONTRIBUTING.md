# Contributing

Thank you for trying the Agentic Mobile Test Framework!

## Quick contribution flow

1. Fork [zykranai/mobTest](https://github.com/zykranai/mobTest)
2. Create a branch: `git checkout -b feature/my-change`
3. Run tests locally: `./scripts/run-tests.sh ios`
4. Open a pull request with a clear description

## Testing your changes

```bash
./scripts/setup-tools.sh
./scripts/build-sample-app.sh ios
ollama serve &
appium &
mvn test -Dagent.heuristic.only=true   # fast smoke
mvn test                               # full Ollama agent
```

Android:

```bash
./scripts/build-sample-app.sh android
mvn test -Dplatform=android -DsuiteXmlFile=testng-android.xml -Dagent.heuristic.only=true
```

## Swapping in your own app

See [README.md](README.md#using-your-own-app) — edit `config.local.properties`, not the committed defaults.
