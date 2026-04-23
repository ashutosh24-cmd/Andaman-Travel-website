#!/bin/bash
# ══════════════════════════════════════════════════════════
# Unfold Andaman - Build & Run Script
# ══════════════════════════════════════════════════════════
# This script compiles all Java source files and starts the
# HTTP server on port 8080.
#
# Usage:
#   chmod +x run.sh    (first time only, to make it executable)
#   ./run.sh           (compile and start the server)
#
# After starting, open http://localhost:8080 in your browser.
# Admin panel is at http://localhost:8080/admin.html
#
# Press Ctrl+C to stop the server.
# ══════════════════════════════════════════════════════════

echo "Stopping any existing server..."
lsof -ti tcp:8080 | xargs kill -9 2>/dev/null

echo "Compiling Unfold Andaman..."

# Create output directory for compiled .class files
mkdir -p out

# Build the classpath — include lib JARs if present, otherwise just compile without them
if ls lib/*.jar 1> /dev/null 2>&1; then
    CLASSPATH="lib/*"
    echo "Found library JARs in lib/ directory."
else
    CLASSPATH=""
    echo "No JARs in lib/ — EmailService will run in simulation mode."
fi

# Compile all Java source files from every package
if [ -n "$CLASSPATH" ]; then
    javac -cp "$CLASSPATH" -d out \
        src/main/java/com/travelgo/models/*.java \
        src/main/java/com/travelgo/dao/*.java \
        src/main/java/com/travelgo/service/*.java \
        src/main/java/com/travelgo/handlers/*.java \
        src/main/java/com/travelgo/server/*.java
else
    javac -d out \
        src/main/java/com/travelgo/models/*.java \
        src/main/java/com/travelgo/dao/*.java \
        src/main/java/com/travelgo/service/*.java \
        src/main/java/com/travelgo/handlers/*.java \
        src/main/java/com/travelgo/server/*.java
fi

# Check if compilation succeeded and start the server
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Starting server..."
    if [ -n "$CLASSPATH" ]; then
        java -cp "out:$CLASSPATH" com.travelgo.server.TravelGoServer
    else
        java -cp "out" com.travelgo.server.TravelGoServer
    fi
else
    echo "Compilation failed!"
    exit 1
fi
