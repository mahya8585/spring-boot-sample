FROM mcr.microsoft.com/devcontainers/java:8

# Install additional tools
RUN apt-get update && apt-get install -y \
    maven \
    git \
    curl \
    vim \
    postgresql-client \
    jq \
    zip \
    unzip

# Install Node.js 12
RUN curl -fsSL https://deb.nodesource.com/setup_12.x | bash - \
    && apt-get install -y nodejs

# Install Azure CLI
RUN curl -sL https://aka.ms/InstallAzureCLIDeb | bash

# Environment variables
ENV JAVA_HOME=/usr/lib/jvm/msopenjdk-current
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

# Development ports
EXPOSE 8080 3000 5432 35729

# Working directory
WORKDIR /workspace