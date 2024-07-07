FROM openjdk:21-slim
WORKDIR /app
LABEL maintainer="dio@gmail.com"
COPY ./target/*.jar app.jar
# EXPOSE 8080
# EXPOSE 9090
ENTRYPOINT ["java","-jar","app.jar"]