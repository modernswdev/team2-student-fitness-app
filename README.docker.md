# Docker Setup — Student Fitness App

This document explains how to build, test, and run the Android app using Docker, without needing Android Studio or a local Android SDK installation.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) (v20+)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2+)
- **KVM** enabled on your host (required by the emulator service — Linux hosts only; see note below)

### KVM availability check

```bash
# Linux: verify KVM is available
sudo apt install cpu-checker
kvm-ok
```

> **macOS / Windows users:** The Android emulator requires KVM (Linux kernel virtualisation). Run it inside a Linux VM or use a cloud/CI environment that exposes `/dev/kvm`.

---

## Services overview

| Service | What it does |
|---------|-------------|
| `build` | Compiles the project and produces the debug APK |
| `test` | Runs JVM unit tests |
| `emulator` | Starts an Android 14 emulator with a web-based VNC viewer |
| `install` | Waits for the emulator to boot, then installs the debug APK |

---

## Building the APK

```bash
docker compose run --rm build
```

The debug APK is written to `app/build/outputs/apk/debug/` on your host machine.

---

## Running Unit Tests

```bash
docker compose run --rm test
```

HTML test reports are written to `app/build/reports/tests/` on your host machine.

---

## Running the Emulator and Viewing the Screen

### 1. Start the emulator

```bash
docker compose up emulator
```

The emulator boots Android 14 (API 34) with a Nexus 5 device profile. First start takes a few minutes.

### 2. Open the VNC viewer

Once the emulator is running, open your browser and go to:

```
http://localhost:6080
```

You can interact with the emulator directly from the browser.

### 3. Build the APK (if you haven't already)

```bash
docker compose run --rm build
```

### 4. Install the app on the emulator

With the emulator still running, open a second terminal and run:

```bash
docker compose run --rm install
```

The `install` service waits for the emulator to finish booting, then installs the APK automatically via ADB. You will see the app appear on the emulator screen in the browser.

### 5. Connect with ADB from your host (optional)

If you have ADB installed locally you can also connect directly:

```bash
adb connect localhost:5555
adb devices          # should list the emulator
```

### 6. Stop the emulator

```bash
docker compose down
```

---

## How It Works

### `Dockerfile`
1. Starts from an **Eclipse Temurin JDK 17** base image (required by AGP 9.x).
2. Installs the **Android SDK command-line tools**, `platform-tools`, `platforms;android-36`, and `build-tools;36.0.0`.
3. Copies the project and runs `./gradlew assembleDebug` to produce the APK.

### `docker-compose.yml`
- **`build`** — builds the debug APK; mounts `app/build/outputs/` to the host.
- **`test`** — runs JVM unit tests; mounts `app/build/reports/` to the host.
- **`emulator`** — runs [`budtmo/docker-android:emulator_14.0`](https://github.com/budtmo/docker-android), an Android 14 emulator with a web VNC interface on port 6080.
- **`install`** — uses the build image's ADB to connect to the emulator and install the APK; delegates to `docker/install-apk.sh`.

### `docker/install-apk.sh`
Polls the emulator via ADB until `sys.boot_completed=1`, then runs `adb install`.

---

## Tips

- First-time emulator pulls and image builds take several minutes. Subsequent starts are much faster.
- Emulator data (installed apps, settings) is persisted in the `emulator-data` Docker volume across restarts.
- To wipe the emulator data: `docker compose down -v`
- To force a clean rebuild of the build image:
  ```bash
  docker compose build --no-cache
  ```
- Instrumented (on-device) Espresso tests (`androidTest/`) require a running emulator. With the emulator service up and the app installed, you could run them via:
  ```bash
  docker compose run --rm build sh -c "./gradlew connectedDebugAndroidTest --no-daemon"
  ```
