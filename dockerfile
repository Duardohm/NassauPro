FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target\NassauPro-1.0.0.jar deploy-nassau-pro-1.0.0.jar
EXPOSE 8080
CMD ["java", "-jar", "deploy-nassau-pro-1.0.0.jar"]