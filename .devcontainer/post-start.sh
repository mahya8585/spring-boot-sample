#!/bin/bash

echo "Post-start script starting..."

# Ensure Java 8 is set as default
if [ -f "/usr/local/sdkman/bin/sdkman-init.sh" ]; then
    echo "Configuring Java 8 with SDKMAN..."
    source "/usr/local/sdkman/bin/sdkman-init.sh"
    
    # Set Java 8 as current version if available
    if [ -d "/usr/local/sdkman/candidates/java/8.0.462-tem" ]; then
        sdk use java 8.0.462-tem
        echo "Java 8.0.462-tem is now active"
    elif [ -d "/usr/local/sdkman/candidates/java/8.0.452-tem" ]; then
        sdk use java 8.0.452-tem
        echo "Java 8.0.452-tem is now active"
    fi
    
    # Verify Java version
    java -version
fi

# Ensure databases are ready
echo "Waiting for databases to be ready..."
sleep 5

# Check PostgreSQL connection
echo "Checking PostgreSQL connection..."
for i in {1..30}; do
    if pg_isready -h db -p 5432 >/dev/null 2>&1; then
        echo "PostgreSQL is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Warning: PostgreSQL is not responding after 30 attempts"
    fi
    sleep 1
done

# Check Redis connection
echo "Checking Redis connection..."
for i in {1..30}; do
    if timeout 3 bash -c "</dev/tcp/redis/6379" >/dev/null 2>&1; then
        echo "Redis is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Warning: Redis is not responding after 30 attempts"
    fi
    sleep 1
done

echo "Post-start script completed."