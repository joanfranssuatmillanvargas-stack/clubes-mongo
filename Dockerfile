# 1) Compila el proyecto con Maven y Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# 2) Imagen final: solo Java y el .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/clubes-mongo-1.0.0.jar app.jar
EXPOSE 8108
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
