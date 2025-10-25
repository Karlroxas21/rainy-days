FROM maven:3.9.4-eclipse-temurin-21 AS build
LABEL authors="Karl Marx Roxas"

COPY .mvn/ .mvn
COPY ./mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# copy jar produiced in builder stage
COPY --from=build /target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]