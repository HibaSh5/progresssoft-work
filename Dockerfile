# ----------------------------------------------------
# Running in Termnial
# -------------------------------
# Postgres
# -------------------------------
#  docker network inspect phase2-postgres
# docker run -d --name spring-app --network phase2-postgres -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/postgres -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=123456 phase2
# -------------------------------
#### docker build -t phase2
####
# -----------------------------------------------------

# --------------------------------------------------------
# Stage 1: Build the Application
# ----------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven pom.xml. This is often cached, speeding up rebuilds.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire source code
COPY src /app/src

# **CRITICAL CHANGE HERE:** Explicitly run spring-boot:repackage
RUN mvn package -DskipTests spring-boot:repackage

# ----------------------------------------------------
# Stage 2: Create the Final Production Image
# ----------------------------------------------------
# Use a minimal JRE image for a smaller runtime footprint
FROM eclipse-temurin:17-jre-focal AS runner

# Set the working directory for the runtime
WORKDIR /app

# Copy the packaged JAR from the first stage (referencing 'builder')
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# The command to run the application using the packaged JAR
ENTRYPOINT ["java", "-jar", "app.jar"]