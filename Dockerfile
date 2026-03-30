#  Build
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime (Optimized for 4GB RAM & Security)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 1. Use JRE instead of JDK (smaller, faster, more secure for production)
# 2. Add a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# 3. Port & Memory Management
# We match your internal port 9090
EXPOSE 9090

# We set the JVM memory limits so Spring doesn't "fight" with the OS for RAM
ENV JAVA_OPTS="-Xmx2g -Xms1g"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]