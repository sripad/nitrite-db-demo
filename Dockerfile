FROM openjdk:8-jdk-alpine
# FROM openjdk:11.0.11-jdk-oraclelinux8
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
