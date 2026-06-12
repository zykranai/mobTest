# Contributing

Thank you for contributing to MobTest!

## Quick flow

1. Fork [zykranai/mobTest](https://github.com/zykranai/mobTest)
2. Create a branch: `git checkout -b feature/my-change`
3. Run tests locally
4. Open a pull request with a clear description

## Testing your changes

```bash
./scripts/setup-environment.sh
./scripts/build-demo-app.sh ios
ollama serve &
appium &
mvn test -Dagent.heuristic.only=true   # fast smoke
mvn test                               # full Ollama agent
```

Android:

```bash
./scripts/build-demo-app.sh android
mvn test -Dplatform=android -DsuiteXmlFile=suites/android.xml -Dagent.heuristic.only=true
```

## Swapping in your own app

See [docs/USAGE.md](docs/USAGE.md) — edit `config.local.properties`, not the committed defaults.
