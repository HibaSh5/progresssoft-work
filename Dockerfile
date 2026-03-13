# =============================
# 1) BUILD STAGE
# docker build -t climoneytransfer .  //build
# docker run -it climoneytransfer  //running
# =============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the project (skip tests)
RUN mvn clean package -DskipTests

# Copy all dependency JARs to a folder
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/lib

# =============================
# 2) RUNTIME STAGE
# =============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy compiled app JAR
COPY --from=build /app/target/climoneytransfer-1.0-SNAPSHOT.jar app.jar

# Copy all dependency JARs
COPY --from=build /app/target/lib ./lib

# Copy accounts.csv exactly where the app expects it
COPY src/accounts.csv ./src/accounts.csv

# Run with full classpath (app + dependencies)
CMD ["java", "-cp", "app.jar:lib/*", "main.Main"]