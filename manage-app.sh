#!/bin/bash

# Portfolio Backend Management Script
# Usage: ./manage-app.sh [start|stop|restart|status|logs|build|deploy|help]

# Configuration
APP_NAME="portfolio-backend"
JAR_FILE="target/portfolio-backend-0.0.1-SNAPSHOT.jar"
PID_FILE="portfolio-backend.pid"
LOG_FILE="portfolio-backend.log"
PORT=8080
HEALTH_URL="http://localhost:8080/api/actuator/health"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}  Portfolio Backend Manager${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Function to check if application is running
is_running() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            return 0
        else
            rm -f "$PID_FILE"
            return 1
        fi
    fi
    return 1
}

# Function to get application status
get_status() {
    if is_running; then
        PID=$(cat "$PID_FILE")
        echo "RUNNING (PID: $PID)"
        return 0
    else
        echo "STOPPED"
        return 1
    fi
}

# Function to check if port is in use
check_port() {
    if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to wait for application to start
wait_for_start() {
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for application to start..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$HEALTH_URL" > /dev/null 2>&1; then
            print_status "Application started successfully!"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "Application failed to start within 60 seconds"
    return 1
}

# Function to start the application
start_app() {
    print_header
    print_status "Starting $APP_NAME..."
    
    if is_running; then
        print_warning "Application is already running (PID: $(cat $PID_FILE))"
        return 1
    fi
    
    if check_port; then
        print_error "Port $PORT is already in use. Please stop the application using this port first."
        return 1
    fi
    
    if [ ! -f "$JAR_FILE" ]; then
        print_error "JAR file not found: $JAR_FILE"
        print_error "Please build the application first using: mvn clean package"
        return 1
    fi
    
    print_status "Starting application on port $PORT..."
    nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
    echo $! > "$PID_FILE"
    
    if wait_for_start; then
        print_status "Application started successfully!"
        print_status "PID: $(cat $PID_FILE)"
        print_status "Logs: $LOG_FILE"
        print_status "Health Check: $HEALTH_URL"
        print_status "API Base URL: http://localhost:8080/api"
        return 0
    else
        print_error "Failed to start application"
        stop_app
        return 1
    fi
}

# Function to stop the application
stop_app() {
    print_header
    print_status "Stopping $APP_NAME..."
    
    if ! is_running; then
        print_warning "Application is not running"
        return 1
    fi
    
    PID=$(cat "$PID_FILE")
    print_status "Stopping application (PID: $PID)..."
    
    # Try graceful shutdown first
    kill $PID
    
    # Wait for graceful shutdown
    local max_attempts=15
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if ! ps -p $PID > /dev/null 2>&1; then
            print_status "Application stopped gracefully"
            rm -f "$PID_FILE"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    # Force kill if graceful shutdown failed
    print_warning "Graceful shutdown failed. Force killing..."
    kill -9 $PID
    sleep 2
    
    if ! ps -p $PID > /dev/null 2>&1; then
        print_status "Application stopped forcefully"
        rm -f "$PID_FILE"
        return 0
    else
        print_error "Failed to stop application"
        return 1
    fi
}

# Function to restart the application
restart_app() {
    print_header
    print_status "Restarting $APP_NAME..."
    
    if is_running; then
        stop_app
        sleep 3
    fi
    
    start_app
}

# Function to show application status
show_status() {
    print_header
    print_status "Application Status: $(get_status)"
    
    if is_running; then
        PID=$(cat "$PID_FILE")
        print_status "PID: $PID"
        print_status "Port: $PORT"
        print_status "Log File: $LOG_FILE"
        print_status "Health Check: $HEALTH_URL"
        
        # Show memory usage
        if command -v ps > /dev/null 2>&1; then
            MEMORY=$(ps -o rss= -p $PID 2>/dev/null | awk '{print $1/1024 " MB"}')
            print_status "Memory Usage: $MEMORY"
        fi
        
        # Test health endpoint
        print_status "Testing health endpoint..."
        if curl -s "$HEALTH_URL" > /dev/null 2>&1; then
            print_status "Health Check: ✓ PASSED"
        else
            print_warning "Health Check: ✗ FAILED"
        fi
    else
        print_status "Application is not running"
    fi
}

# Function to show logs
show_logs() {
    print_header
    print_status "Showing application logs..."
    
    if [ ! -f "$LOG_FILE" ]; then
        print_error "Log file not found: $LOG_FILE"
        return 1
    fi
    
    if [ "$1" = "-f" ] || [ "$1" = "--follow" ]; then
        print_status "Following logs (Press Ctrl+C to stop)..."
        tail -f "$LOG_FILE"
    else
        print_status "Last 50 lines of logs:"
        tail -50 "$LOG_FILE"
    fi
}


# Function to show help
show_help() {
    print_header
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start     Start the application"
    echo "  stop      Stop the application"
    echo "  restart   Restart the application"
    echo "  status    Show application status"
    echo "  logs      Show application logs"
    echo "  logs -f   Follow application logs"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start"
    echo "  $0 status"
    echo "  $0 logs -f"
    echo ""
    echo "Configuration:"
    echo "  JAR File: $JAR_FILE"
    echo "  Port: $PORT"
    echo "  PID File: $PID_FILE"
    echo "  Log File: $LOG_FILE"
}

# Main script logic
case "$1" in
    start)
        start_app
        ;;
    stop)
        stop_app
        ;;
    restart)
        restart_app
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs "$2"
        ;;
    help|--help|-h)
        show_help
        ;;
    "")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac

exit $?
