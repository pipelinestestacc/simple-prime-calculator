#
# A multi-stage build of a simple Java application.
#

# Stage 1: Build the application using Gradle
FROM gradle:latest as builder
WORKDIR /app

# -- Copy the Gradle build files & source code, then build the app
COPY build.gradle /app/
COPY src /app/src
RUN gradle build --no-daemon

# Stage 2: Create the final image with JDK only, no Gradle
FROM openjdk:latest

#  -- Copy the built JAR file from the previous stage
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/simple-prime-numbers-calculator.jar

VOLUME /app/config

# Application start up
CMD ["java", "-jar", "simple-prime-numbers-calculator.jar", "10"]
