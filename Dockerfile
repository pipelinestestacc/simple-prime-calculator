#
# Copies from repository available to Pipelines Test Account: https://github.com/AndreyKoltsov1997/simple-prime-calculator
# A multi-stage build of a simple Java application.
#

# Stage 1: Build the application using Gradle
FROM gradle:8.7.0-jdk17 as builder
WORKDIR /app

# -- Copy the Gradle build files & source code, then build the app
COPY build.gradle /app/
COPY src /app/src
RUN gradle build --no-daemon

# Stage 2: Create the final image with JDK only, no Gradle
FROM openjdk:17-alpine

#  -- Copy the built JAR file from the previous stage
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/simple-prime-numbers-calculator.jar

VOLUME /app/config

# Application start up
CMD ["java", "-jar", "simple-prime-numbers-calculator.jar", "10"]
