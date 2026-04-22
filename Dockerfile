# Android build environment
# Builds the debug APK for the Student Fitness App
FROM eclipse-temurin:17-jdk-jammy

# Install required tools
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Set Android SDK environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH="${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${PATH}"

# Download and install Android command-line tools
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
         -O /tmp/cmdline-tools.zip && \
    unzip -q /tmp/cmdline-tools.zip -d /tmp/cmdline-tools-extract && \
    mv /tmp/cmdline-tools-extract/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm /tmp/cmdline-tools.zip

# Accept Android SDK licenses and install required SDK components
RUN yes | sdkmanager --licenses > /dev/null && \
    sdkmanager \
        "platform-tools" \
        "platforms;android-36" \
        "build-tools;36.0.0"

WORKDIR /app

# Copy Gradle wrapper and version catalog first to cache dependencies
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/

# Normalize shell scripts from Windows checkouts and pre-download Gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew --version

# Keep install script in the image (avoids host line-ending issues on bind mounts)
COPY docker/install-apk.sh /usr/local/bin/install-apk.sh
RUN sed -i 's/\r$//' /usr/local/bin/install-apk.sh && chmod +x /usr/local/bin/install-apk.sh

# Copy the rest of the project
COPY app/ app/

# Build the debug APK
RUN ./gradlew assembleDebug --no-daemon

# Default command: display the built APK location
CMD ["find", "app/build/outputs/apk", "-name", "*.apk"]
