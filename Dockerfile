# Build stage: Use Maven image (no wrapper needed)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy project definition and download dependencies (cached layer)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code and build the JAR file, skipping tests for speed
COPY src ./src
RUN mvn package -DskipTests

#------------------------------------------------------------------

# Run stage: Use a smaller JRE image for the final container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# FIX: Render requires the app to listen on port 10000
ENV SERVER_PORT=10000
EXPOSE 10000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
