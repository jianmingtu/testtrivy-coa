FROM eclipse-temurin:17-jre-alpine

RUN apk upgrade libexpat  # Fix for CVE-2022-43680

COPY ./target/testtrivy-application.jar testtrivy-application.jar

ENTRYPOINT ["java", "-jar","/app/testtrivy-application.jar"]
