FROM eclipse-temurin:11-jre

WORKDIR /app
COPY target/expense-tracker-1.0.jar app.jar
COPY text.txt text.txt

CMD ["sh", "-c", "cat text.txt | java -jar app.jar"]

