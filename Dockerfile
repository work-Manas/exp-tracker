FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/expense-tracker-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
