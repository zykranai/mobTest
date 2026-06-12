# Contributing

Thank you for contributing to MobTest!

**Documentation:** [docs/README.md](docs/README.md) · [GETTING_STARTED.md](GETTING_STARTED.md) · [docs/FAQ.md](docs/FAQ.md)

## Quick flow

1. Fork [zykranai/mobTest](https://github.com/zykranai/mobTest)
2. Create a branch: `git checkout -b feature/my-change`
3. Run tests locally
4. Open a pull request with a clear description

## Testing your changes

```bash
./scripts/setup-environment.sh
./scripts/run-tests.sh ios --heuristic    # full stable workflow, fast mode
./scripts/run-tests.sh ios                # with Ollama
```

Android:

```bash
./scripts/run-tests.sh android --heuristic
```

Manual:

```bash
mvn test -DsuiteXmlFile=suites/ios.xml -Dagent.heuristic.only=true
mvn test -DsuiteXmlFile=suites/android.xml
```

## Swapping in your own app

See [docs/USAGE.md](docs/USAGE.md) — edit `config.local.properties`, not the committed defaults.
