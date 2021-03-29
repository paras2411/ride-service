FROM openjdk:15-jdk
COPY ./target/ride-service-0.0.1-SNAPSHOT.jar ride-service.jar
ENTRYPOINT ["java", "-jar", "ride-service.jar"]