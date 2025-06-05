# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add a non-root user
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 