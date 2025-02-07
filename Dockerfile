# Stage 1: Build the project
FROM maven:3.8.6-openjdk-17-slim AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and install dependencies (this will cache dependencies if not changed)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src /app/src

# Package the application into a JAR file
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/Event_Management_System-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
