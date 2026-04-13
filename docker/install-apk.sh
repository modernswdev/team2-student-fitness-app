#!/usr/bin/env bash
# Waits for the Android emulator to finish booting, then installs the debug APK.
# Expected environment variables:
#   EMULATOR_HOST      - hostname/IP of the emulator container (default: emulator)
#   EMULATOR_ADB_PORT  - ADB port exposed by the emulator container (default: 5555)
set -euo pipefail

EMULATOR_HOST="${EMULATOR_HOST:-emulator}"
EMULATOR_ADB_PORT="${EMULATOR_ADB_PORT:-5555}"
SERIAL="${EMULATOR_HOST}:${EMULATOR_ADB_PORT}"

APK_PATH=$(find /app/app/build/outputs/apk/debug -name "*.apk" 2>/dev/null | head -1 || true)

if [ -z "$APK_PATH" ]; then
  echo "ERROR: No debug APK found under app/build/outputs/apk/debug/."
  echo "       Run 'docker compose run --rm build' first to build the APK."
  exit 1
fi

echo "APK to install: $APK_PATH"
echo "Connecting ADB to ${SERIAL}..."

# Retry ADB connect until the emulator container's ADB server is reachable
until adb connect "$SERIAL" 2>&1 | grep -q "connected"; do
  echo "  ADB not reachable yet — retrying in 5 s..."
  sleep 5
done

echo "ADB connected. Waiting for the emulator to finish booting..."

until adb -s "$SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '[:space:]' | grep -q "1"; do
  echo "  Emulator still booting — retrying in 5 s..."
  sleep 5
done

echo "Emulator ready!"
echo "Installing APK..."
adb -s "$SERIAL" install -r "$APK_PATH"
echo "Done — app installed successfully on the emulator."
