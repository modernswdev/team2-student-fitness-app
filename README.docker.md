# Docker Setup — Student Fitness App

This document explains how to build and test the Android app using Docker, without needing Android Studio or a local Android SDK installation.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) (v20+)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2+)

---

## Building the APK

### Using Docker Compose (recommended)

```bash
docker compose run --rm build
```

The debug APK will be written to `app/build/outputs/apk/debug/` on your host machine.

### Using Docker directly

```bash
# Build the image
docker build -t student-fitness-app .

# Run the container and copy the APK out
docker run --rm -v "$(pwd)/app/build/outputs:/app/app/build/outputs" student-fitness-app
```

---

## Running Unit Tests

```bash
docker compose run --rm test
```

Test reports (HTML) will be written to `app/build/reports/tests/` on your host machine.

---

## How It Works

The `Dockerfile`:
1. Starts from an **Eclipse Temurin JDK 17** base image (required by AGP 9.x).
2. Installs the **Android SDK command-line tools**, `platform-tools`, `platforms;android-36`, and `build-tools;36.0.0`.
3. Copies the project and runs `./gradlew assembleDebug` to produce the APK.

The `docker-compose.yml` defines two services:
- **`build`** — builds the debug APK and mounts the output directory to the host.
- **`test`** — runs JVM unit tests (`./gradlew test`) and mounts the report directory to the host.

---

## Tips

- First-time builds take several minutes while Gradle and the Android SDK are downloaded and cached inside the image. Subsequent builds reuse the cached layers.
- To force a clean rebuild of the Docker image:
  ```bash
  docker compose build --no-cache
  ```
- Instrumented (on-device) tests require a connected Android device or emulator and cannot be run inside Docker.
