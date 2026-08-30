FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/*SNAPSHOT.jar app.jar
EXPOSE 5003
ENTRYPOINT ["java", "-jar", "app.jar"]