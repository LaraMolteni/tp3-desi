# This Dockerfile is no longer needed for MySQL-only setup.
# You can remove or ignore this file if you are not containerizing the app itself.

# Use Maven to build the app, then run it with Java
# FROM maven:3.9.7-eclipse-temurin-21 AS build
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# FROM eclipse-temurin:21-jre
# WORKDIR /app
# COPY --from=build /app/target/tp3-desi-0.0.1-SNAPSHOT.war app.war
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.war"]
