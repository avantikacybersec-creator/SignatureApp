FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw clean package

EXPOSE 8080

ENTRYPOINT ["java","-jar","target/signatureapp-0.0.1-SNAPSHOT.jar"]