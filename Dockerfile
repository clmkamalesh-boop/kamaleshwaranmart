# ==============================================================================
# Production Dockerfile for KamaleshwaranMart
# Runtime: Apache Tomcat 9.0 on Eclipse Temurin OpenJDK 17
# Cloud Platform: Render (supports dynamic $PORT variable)
# ==============================================================================

# Stage 1: Build the Maven WAR package using OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy POM and project source
COPY pom.xml .
COPY src ./src

# Build production WAR skipping unit tests
RUN mvn clean package -DskipTests

# Stage 2: Production Apache Tomcat 9 Runtime
FROM tomcat:9.0-jdk17-temurin
LABEL maintainer="Kamaleshwaran"
LABEL description="KamaleshwaranMart Multi-seller E-Commerce Marketplace"

# Remove default Tomcat web applications for security & performance
RUN rm -rf /usr/local/tomcat/webapps/*

# Ensure data directory exists with write permissions for embedded H2 database
RUN mkdir -p /usr/local/tomcat/data && chmod 777 /usr/local/tomcat/data

# Deploy WAR to ROOT.war (serves at /) and kamaleshwaranmart.war (serves at /kamaleshwaranmart/)
COPY --from=builder /workspace/target/kamaleshwaranmart.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=builder /workspace/target/kamaleshwaranmart.war /usr/local/tomcat/webapps/kamaleshwaranmart.war

# Default environment configuration
ENV DB_TYPE=h2
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Expose port (Render injects $PORT, defaults to 8080)
EXPOSE 8080

# Dynamically configure Tomcat to listen on Render's $PORT and start catalina
CMD ["sh", "-c", "APP_PORT=${PORT:-8080}; echo \"Configuring Tomcat to listen on port $APP_PORT\"; sed -i \"s/port=\\\"8080\\\"/port=\\\"$APP_PORT\\\"/g\" /usr/local/tomcat/conf/server.xml; exec catalina.sh run"]
