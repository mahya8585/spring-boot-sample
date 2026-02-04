#!/bin/bash

echo "Post-create script starting..."

# Install frontend dependencies
if [ -f "/workspace/frontend/package.json" ]; then
    echo "Installing frontend dependencies..."
    cd /workspace/frontend
    npm install
fi

# Make Maven wrapper executable
if [ -f "/workspace/backend/mvnw" ]; then
    chmod +x /workspace/backend/mvnw
fi

# Create VS Code settings for Java development
echo "Setting up VS Code Java configuration..."
VSCODE_DIR="/workspace/.vscode"
SETTINGS_FILE="$VSCODE_DIR/settings.json"

# Create .vscode directory if it doesn't exist
mkdir -p "$VSCODE_DIR"

# Setup Java 8 using SDKMAN if available
if [ -f "/usr/local/sdkman/bin/sdkman-init.sh" ]; then
    echo "Setting up Java 8 with SDKMAN..."
    source "/usr/local/sdkman/bin/sdkman-init.sh"
    
    # Check if Java 8 is already installed
    if [ -d "/usr/local/sdkman/candidates/java/8.0.462-tem" ]; then
        echo "Java 8.0.462-tem found, setting as default..."
        sdk default java 8.0.462-tem
        JAVA_HOME_PATH="/usr/local/sdkman/candidates/java/8.0.462-tem"
    elif [ -d "/usr/local/sdkman/candidates/java/8.0.452-tem" ]; then
        echo "Java 8.0.452-tem found, setting as default..."
        sdk default java 8.0.452-tem
        JAVA_HOME_PATH="/usr/local/sdkman/candidates/java/8.0.452-tem"
    else
        echo "Installing Java 8..."
        sdk install java 8.0.462-tem < /dev/null
        sdk default java 8.0.462-tem
        JAVA_HOME_PATH="/usr/local/sdkman/candidates/java/8.0.462-tem"
    fi
else
    # Fallback to traditional Java detection
    JAVA_HOME_PATH=""
    if [ -d "/usr/lib/jvm/msopenjdk-current" ]; then
        JAVA_HOME_PATH="/usr/lib/jvm/msopenjdk-current"
    elif [ -d "/usr/local/sdkman/candidates/java/current" ]; then
        JAVA_HOME_PATH="/usr/local/sdkman/candidates/java/current"
    elif [ ! -z "$JAVA_HOME" ]; then
        JAVA_HOME_PATH="$JAVA_HOME"
    else
        echo "Warning: Could not detect Java Home path"
        JAVA_HOME_PATH="/usr/lib/jvm/msopenjdk-current"
    fi
fi

echo "Java Home set to: $JAVA_HOME_PATH"

# Create or update VS Code settings.json
cat > "$SETTINGS_FILE" << EOF
{
    "java.jdt.ls.java.home": "$JAVA_HOME_PATH",
    "java.home": "$JAVA_HOME_PATH",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-1.8",
            "path": "$JAVA_HOME_PATH",
            "default": true
        }
    ],
    "maven.executable.path": "/usr/bin/mvn"
}
EOF

echo "VS Code Java settings created at $SETTINGS_FILE"

# Add SDKMAN initialization and Java 8 setup to user's shell profile
echo "Configuring shell profile for automatic Java 8 setup..."
BASHRC_FILE="/home/vscode/.bashrc"

# Add SDKMAN initialization and Java 8 setup if not already present
if ! grep -q "# Auto-setup Java 8 for TechBookStore" "$BASHRC_FILE"; then
    cat >> "$BASHRC_FILE" << 'EOF'

# Auto-setup Java 8 for TechBookStore
if [ -f "/usr/local/sdkman/bin/sdkman-init.sh" ]; then
    source "/usr/local/sdkman/bin/sdkman-init.sh"
    
    # Set Java 8 as current version if available
    if [ -d "/usr/local/sdkman/candidates/java/8.0.462-tem" ]; then
        sdk use java 8.0.462-tem >/dev/null 2>&1
    elif [ -d "/usr/local/sdkman/candidates/java/8.0.452-tem" ]; then
        sdk use java 8.0.452-tem >/dev/null 2>&1
    fi
fi
EOF
    echo "Added Java 8 auto-setup to .bashrc"
fi

echo "Post-create script completed."