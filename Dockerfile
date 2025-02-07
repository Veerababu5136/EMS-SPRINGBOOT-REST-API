# Step 1: Use Maven image to build the application
FROM maven:3.8.6-jdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml to the container and download dependencies
COPY pom.xml .

# Download dependencies and cache them to avoid re-downloading on each build
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src /app/src

# Build the project (this creates the JAR file)
RUN mvn clean package -DskipTests

# Step 2: Use OpenJDK to create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory for the runtime image
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/Event_Management_System-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
