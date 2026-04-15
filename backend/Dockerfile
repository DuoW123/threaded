FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
EXPOSE 10000
CMD ["java", "-Dserver.port=10000", "-jar", "target/threaded-app-0.0.1-SNAPSHOT.jar"]