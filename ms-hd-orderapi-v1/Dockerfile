# Base image with no known vulnerabilities
FROM ubuntu:20.04

# Install necessary packages.
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk
ADD target/order-1.1.1-SNAPSHOT.jar app.jar
ARG config_path
COPY ABAP_AS1.jcoDestination .
COPY ABAP_AS1_MESSAGE_SERVER.jcoDestination .
COPY ABAP_AS1_DEV_MESSAGE_SERVER.jcoDestination .
COPY ABAP_AS1_DEV.jcoDestination .
COPY ABAP_AS1_PROD.jcoDestination .
RUN mkdir -p /app
COPY lib/libsapjco3.so /app
COPY lib/sapjco3.jar /app
ENV LD_LIBRARY_PATH="/app"
ENV PATH=$PATH:/app
ENV CLASSPATH="/app/sapjco3.jar"
ADD $config_path config.properties
ENV CONFIG_FILE ./config.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]