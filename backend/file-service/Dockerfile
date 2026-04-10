FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend ./backend
RUN mvn -B -pl backend/file-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
RUN useradd -r -u 1001 spring
USER spring
WORKDIR /app
COPY --from=build app/backend/file-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
