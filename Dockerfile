FROM eclipse-temurin:24-jdk

RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the agent skills directory from your project root into the container
COPY .agent /app/.agent

# Recursively find and make all shell scripts executable
RUN find /app/.agent -type f -name "*.sh" -exec chmod +x {} \;
