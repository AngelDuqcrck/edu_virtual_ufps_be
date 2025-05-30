FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/edu_virtual_ufps-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} edu_virtual_ufps.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "edu_virtual_ufps.jar"]