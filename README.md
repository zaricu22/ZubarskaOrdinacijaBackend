Render Deployment (render branch)

The render branch is used exclusively for cloud deployment on Render. It contains only two files:

- Dockerfile — tells Docker how to run the JAR
- Pre-built .jar — committed directly (no build step in CI)

How it works

Render is configured via the dashboard:
- Branch: render
- Dockerfile Path: ./Dockerfile

When a new commit is pushed to the render branch, Render detects it (by default), runs docker build using the Dockerfile, and redeploys the service.

Updating the deployment

1. Build the JAR locally:
mvn clean package -DskipTests
2. Switch to the render branch and copy the new JAR
3. Push to origin render
