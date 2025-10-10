#!/bin/bash

# Portfolio Backend Launcher Script
# Provides easy access to all management scripts

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}  Portfolio Backend Launcher${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_menu() {
    echo ""
    echo -e "${GREEN}Available Management Scripts:${NC}"
    echo ""
    echo "1. Basic Management (manage-app.sh)"
    echo "   - Start, stop, restart, status, logs"
    echo ""
    echo "2. Advanced Management (advanced-manage.sh)"
    echo "   - All basic features plus metrics, backups, database management"
    echo ""
    echo "3. Quick Start"
    echo "   - Start the application directly"
    echo ""
    echo "4. Quick Status"
    echo "   - Check application status"
    echo ""
    echo "5. Exit"
    echo ""
}

run_basic_management() {
    echo ""
    echo -e "${YELLOW}Starting Basic Management Script...${NC}"
    echo ""
    ./manage-app.sh help
    echo ""
    echo "Enter command (start|stop|restart|status|logs|help): "
    read -r command
    ./manage-app.sh "$command"
}

run_advanced_management() {
    echo ""
    echo -e "${YELLOW}Starting Advanced Management Script...${NC}"
    echo ""
    ./advanced-manage.sh help
    echo ""
    echo "Enter command: "
    read -r command
    ./advanced-manage.sh "$command"
}

quick_start() {
    echo ""
    echo -e "${YELLOW}Quick Start - Starting Application...${NC}"
    ./manage-app.sh start
}

quick_status() {
    echo ""
    echo -e "${YELLOW}Quick Status Check...${NC}"
    ./manage-app.sh status
}

# Main launcher logic
print_header

# Check if we're in the right directory
if [ ! -f "manage-app.sh" ] || [ ! -f "advanced-manage.sh" ]; then
    echo -e "${RED}Error: Management scripts not found.${NC}"
    echo "Please run this script from the backend directory."
    exit 1
fi

# Make scripts executable
chmod +x manage-app.sh advanced-manage.sh 2>/dev/null

while true; do
    print_menu
    echo -n "Select an option (1-5): "
    read -r choice
    
    case $choice in
        1)
            run_basic_management
            ;;
        2)
            run_advanced_management
            ;;
        3)
            quick_start
            ;;
        4)
            quick_status
            ;;
        5)
            echo ""
            echo -e "${GREEN}Goodbye!${NC}"
            exit 0
            ;;
        *)
            echo ""
            echo -e "${RED}Invalid option. Please select 1-5.${NC}"
            ;;
    esac
    
    echo ""
    echo -e "${YELLOW}Press Enter to continue...${NC}"
    read -r
done
