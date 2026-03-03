# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy only pom first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Render uses 10000 internally)
EXPOSE 8080

# Run with forced Mongo URI injection
ENTRYPOINT ["sh", "-c", "java -Dspring.data.mongodb.uri=$SPRING_DATA_MONGODB_URI -Dserver.port=$PORT -jar app.jar"]