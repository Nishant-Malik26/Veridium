FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/auth-service.jar auth-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","auth-service.jar"]
