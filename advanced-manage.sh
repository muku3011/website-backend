#!/bin/bash

# Advanced Portfolio Backend Management Script
# Usage: ./advanced-manage.sh [COMMAND] [OPTIONS]

# Configuration
APP_NAME="portfolio-backend"
JAR_FILE="target/website-backend.jar"
PID_FILE="portfolio-backend.pid"
LOG_FILE="portfolio-backend.log"
CONFIG_FILE="application.yml"
BACKUP_DIR="backups"
PORT=8080
HEALTH_URL="http://localhost:8080/api/actuator/health"
METRICS_URL="http://localhost:8080/api/actuator/metrics"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

# Logging functions
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_debug() { echo -e "${CYAN}[DEBUG]${NC} $1"; }
log_header() { echo -e "${BLUE}================================${NC}"; echo -e "${BLUE}  $1${NC}"; echo -e "${BLUE}================================${NC}"; }

# Utility functions
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

get_pid() {
    if [ -f "$PID_FILE" ]; then
        cat "$PID_FILE"
    else
        echo "N/A"
    fi
}

check_port() {
    if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

wait_for_health() {
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$HEALTH_URL" > /dev/null 2>&1; then
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    return 1
}

# Backup functions
create_backup() {
    local backup_name="backup-$(date +%Y%m%d-%H%M%S)"
    local backup_path="$BACKUP_DIR/$backup_name"
    
    log_info "Creating backup: $backup_name"
    
    mkdir -p "$backup_path"
    
    # Backup JAR file
    if [ -f "$JAR_FILE" ]; then
        cp "$JAR_FILE" "$backup_path/"
    fi
    
    # Backup configuration
    if [ -f "$CONFIG_FILE" ]; then
        cp "$CONFIG_FILE" "$backup_path/"
    fi
    
    # Backup database
    if [ -f "portfolio.db" ]; then
        cp "portfolio.db" "$backup_path/"
    fi
    
    # Create backup info
    cat > "$backup_path/backup-info.txt" << EOF
Backup created: $(date)
Application: $APP_NAME
JAR File: $JAR_FILE
PID: $(get_pid)
Status: $(is_running && echo "RUNNING" || echo "STOPPED")
EOF
    
    log_info "Backup created successfully: $backup_path"
    echo "$backup_path"
}

list_backups() {
    if [ -d "$BACKUP_DIR" ]; then
        log_info "Available backups:"
        ls -la "$BACKUP_DIR" | grep "^d" | awk '{print $9}' | grep -v "^\.$\|^\.\.$" | sort -r
    else
        log_warn "No backups directory found"
    fi
}

# Monitoring functions
show_metrics() {
    log_header "Application Metrics"
    
    if ! is_running; then
        log_error "Application is not running"
        return 1
    fi
    
    # JVM Metrics
    PID=$(get_pid)
    if [ "$PID" != "N/A" ]; then
        log_info "JVM Metrics (PID: $PID):"
        
        # Memory usage
        if command -v ps > /dev/null 2>&1; then
            MEMORY=$(ps -o rss= -p $PID 2>/dev/null | awk '{print $1/1024 " MB"}')
            log_info "Memory Usage: $MEMORY"
        fi
        
        # CPU usage
        if command -v top > /dev/null 2>&1; then
            CPU=$(top -p $PID -n 1 -b | grep $PID | awk '{print $9}')
            log_info "CPU Usage: $CPU%"
        fi
    fi
    
    # Application metrics via actuator
    log_info "Application Metrics:"
    if curl -s "$METRICS_URL" > /dev/null 2>&1; then
        # Get specific metrics
        curl -s "$METRICS_URL/jvm.memory.used" 2>/dev/null | jq -r '.measurements[0].value' 2>/dev/null | awk '{print "JVM Memory Used: " $1/1024/1024 " MB"}' || log_warn "Could not fetch JVM memory metrics"
        curl -s "$METRICS_URL/http.server.requests" 2>/dev/null | jq -r '.measurements[0].value' 2>/dev/null | awk '{print "HTTP Requests: " $1}' || log_warn "Could not fetch HTTP request metrics"
    else
        log_warn "Metrics endpoint not available"
    fi
}

show_logs() {
    local lines=${1:-50}
    local follow=${2:-false}
    
    if [ ! -f "$LOG_FILE" ]; then
        log_error "Log file not found: $LOG_FILE"
        return 1
    fi
    
    if [ "$follow" = "true" ]; then
        log_info "Following logs (Press Ctrl+C to stop)..."
        tail -f "$LOG_FILE"
    else
        log_info "Last $lines lines of logs:"
        tail -$lines "$LOG_FILE"
    fi
}

# Database management
backup_database() {
    if [ -f "portfolio.db" ]; then
        local backup_name="db-backup-$(date +%Y%m%d-%H%M%S).db"
        cp "portfolio.db" "$backup_name"
        log_info "Database backed up to: $backup_name"
    else
        log_warn "Database file not found"
    fi
}

reset_database() {
    log_warn "This will delete the current database and recreate it with sample data!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if is_running; then
            log_info "Stopping application..."
            stop_app
        fi
        
        if [ -f "portfolio.db" ]; then
            backup_database
            rm "portfolio.db"
            log_info "Database deleted"
        fi
        
        log_info "Starting application to recreate database..."
        start_app
    else
        log_info "Database reset cancelled"
    fi
}

# Configuration management
show_config() {
    log_header "Application Configuration"
    
    if [ -f "$CONFIG_FILE" ]; then
        log_info "Configuration file: $CONFIG_FILE"
        echo
        cat "$CONFIG_FILE"
    else
        log_warn "Configuration file not found: $CONFIG_FILE"
    fi
}

edit_config() {
    if [ -f "$CONFIG_FILE" ]; then
        ${EDITOR:-nano} "$CONFIG_FILE"
        log_info "Configuration updated. Restart application to apply changes."
    else
        log_error "Configuration file not found: $CONFIG_FILE"
    fi
}

# Main application management functions
start_app() {
    log_header "Starting $APP_NAME"
    
    if is_running; then
        log_warn "Application is already running (PID: $(get_pid))"
        return 1
    fi
    
    if check_port; then
        log_error "Port $PORT is already in use"
        return 1
    fi
    
    if [ ! -f "$JAR_FILE" ]; then
        log_error "JAR file not found: $JAR_FILE"
        log_error "Please build the application first using: mvn clean package"
        return 1
    fi
    
    log_info "Starting application on port $PORT..."
    nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
    echo $! > "$PID_FILE"
    
    if wait_for_health; then
        log_info "Application started successfully!"
        log_info "PID: $(get_pid)"
        log_info "Health Check: $HEALTH_URL"
        return 0
    else
        log_error "Application failed to start"
        stop_app
        return 1
    fi
}

stop_app() {
    log_header "Stopping $APP_NAME"
    
    if ! is_running; then
        log_warn "Application is not running"
        return 1
    fi
    
    PID=$(get_pid)
    log_info "Stopping application (PID: $PID)..."
    
    kill $PID
    
    # Wait for graceful shutdown
    local max_attempts=15
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if ! ps -p $PID > /dev/null 2>&1; then
            log_info "Application stopped gracefully"
            rm -f "$PID_FILE"
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    
    # Force kill
    log_warn "Force killing application..."
    kill -9 $PID
    rm -f "$PID_FILE"
    log_info "Application stopped"
}

restart_app() {
    log_header "Restarting $APP_NAME"
    
    if is_running; then
        stop_app
        sleep 3
    fi
    
    start_app
}


show_status() {
    log_header "Application Status"
    
    local status=$(is_running && echo "RUNNING" || echo "STOPPED")
    log_info "Status: $status"
    
    if is_running; then
        log_info "PID: $(get_pid)"
        log_info "Port: $PORT"
        log_info "Log File: $LOG_FILE"
        
        # Health check
        if curl -s "$HEALTH_URL" > /dev/null 2>&1; then
            log_info "Health Check: ✓ PASSED"
        else
            log_warn "Health Check: ✗ FAILED"
        fi
        
        # Show basic metrics
        show_metrics
    fi
}

# Help function
show_help() {
    log_header "Advanced Portfolio Backend Manager"
    
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Application Management:"
    echo "  start                 Start the application"
    echo "  stop                  Stop the application"
    echo "  restart               Restart the application"
    echo "  status                Show application status and metrics"
    echo ""
    echo "Monitoring & Logs:"
    echo "  logs [lines]          Show last N lines of logs (default: 50)"
    echo "  logs -f               Follow logs in real-time"
    echo "  metrics               Show application metrics"
    echo ""
    echo "Database Management:"
    echo "  db-backup             Backup the database"
    echo "  db-reset              Reset database (with confirmation)"
    echo ""
    echo "Configuration:"
    echo "  config                Show current configuration"
    echo "  config-edit           Edit configuration file"
    echo ""
    echo "Backup & Recovery:"
    echo "  backup                Create application backup"
    echo "  backups               List available backups"
    echo ""
    echo "Examples:"
    echo "  $0 start"
    echo "  $0 logs 100"
    echo "  $0 logs -f"
    echo "  $0 metrics"
    echo "  $0 db-backup"
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
        show_logs "$2" "$3"
        ;;
    metrics)
        show_metrics
        ;;
    backup)
        create_backup
        ;;
    backups)
        list_backups
        ;;
    db-backup)
        backup_database
        ;;
    db-reset)
        reset_database
        ;;
    config)
        show_config
        ;;
    config-edit)
        edit_config
        ;;
    help|--help|-h)
        show_help
        ;;
    "")
        show_help
        ;;
    *)
        log_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac

exit $?
