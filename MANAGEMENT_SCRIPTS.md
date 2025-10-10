# Portfolio Backend Management Scripts

This directory contains comprehensive shell scripts to manage the Spring Boot JAR application with start, stop, check, and other management functions.

## 📁 Available Scripts

### 1. **launcher.sh** - Interactive Launcher
- **Purpose**: Easy access to all management scripts
- **Usage**: `./launcher.sh`
- **Features**: Interactive menu to choose between different management options

### 2. **manage-app.sh** - Basic Management
- **Purpose**: Core application management functions
- **Usage**: `./manage-app.sh [COMMAND]`
- **Platform**: Linux/macOS

### 4. **advanced-manage.sh** - Advanced Management
- **Purpose**: Extended management with monitoring, backups, and database management
- **Usage**: `./advanced-manage.sh [COMMAND] [OPTIONS]`
- **Platform**: Linux/macOS

## 🚀 Quick Start

### Option 1: Interactive Launcher (Recommended)
```bash
cd backend
./launcher.sh
```

### Option 2: Direct Script Usage
```bash
cd backend
./manage-app.sh start
```

## 📋 Available Commands

### Basic Management Commands
| Command | Description | Example |
|---------|-------------|---------|
| `start` | Start the application | `./manage-app.sh start` |
| `stop` | Stop the application | `./manage-app.sh stop` |
| `restart` | Restart the application | `./manage-app.sh restart` |
| `status` | Show application status | `./manage-app.sh status` |
| `logs` | Show application logs | `./manage-app.sh logs` |
| `logs -f` | Follow logs in real-time | `./manage-app.sh logs -f` |
| `help` | Show help message | `./manage-app.sh help` |

### Advanced Management Commands
| Command | Description | Example |
|---------|-------------|---------|
| `start` | Start the application | `./advanced-manage.sh start` |
| `stop` | Stop the application | `./advanced-manage.sh stop` |
| `restart` | Restart the application | `./advanced-manage.sh restart` |
| `status` | Show detailed status and metrics | `./advanced-manage.sh status` |
| `logs [lines]` | Show last N lines of logs | `./advanced-manage.sh logs 100` |
| `logs -f` | Follow logs in real-time | `./advanced-manage.sh logs -f` |
| `metrics` | Show application metrics | `./advanced-manage.sh metrics` |
| `backup` | Create application backup | `./advanced-manage.sh backup` |
| `backups` | List available backups | `./advanced-manage.sh backups` |
| `db-backup` | Backup the database | `./advanced-manage.sh db-backup` |
| `db-reset` | Reset database (with confirmation) | `./advanced-manage.sh db-reset` |
| `config` | Show current configuration | `./advanced-manage.sh config` |
| `config-edit` | Edit configuration file | `./advanced-manage.sh config-edit` |

## ⚙️ Configuration

### Default Settings
- **JAR File**: `target/website-backend.jar`
- **Port**: `8080`
- **PID File**: `portfolio-backend.pid`
- **Log File**: `portfolio-backend.log`
- **Health URL**: `http://localhost:8080/api/actuator/health`

### Customizing Configuration
You can modify the configuration variables at the top of each script:

```bash
# In manage-app.sh or advanced-manage.sh
APP_NAME="portfolio-backend"
JAR_FILE="target/website-backend.jar"
PORT=8080
HEALTH_URL="http://localhost:8080/api/actuator/health"
```

## 🔧 Prerequisites

### Required Software
- **Java 17 or higher**
- **Maven 3.6 or higher** (for building)
- **curl** (for health checks)
- **jq** (optional, for advanced metrics parsing)

### Build Requirements
Before using the management scripts, you need to build the application:

```bash
cd backend
mvn clean package
```

## 📊 Features

### Basic Management Features
- ✅ **Start/Stop/Restart** application
- ✅ **Status monitoring** with health checks
- ✅ **Log viewing** with follow option
- ✅ **Port conflict detection**
- ✅ **Graceful shutdown** with force kill fallback
- ✅ **PID file management**
- ✅ **Colored output** for better readability

### Advanced Management Features
- ✅ **All basic features**
- ✅ **Application metrics** (memory, CPU, JVM)
- ✅ **Backup management** (application, database)
- ✅ **Database operations** (backup, reset)
- ✅ **Configuration management**
- ✅ **Health endpoint monitoring**
- ✅ **Automatic backup before deployment**

## 🖥️ Platform Support

### Linux/macOS
- `manage-app.sh` - Basic management
- `advanced-manage.sh` - Advanced management
- `launcher.sh` - Interactive launcher

## 📝 Usage Examples

### Starting the Application
```bash
# Using launcher
./launcher.sh
# Select option 3 (Quick Start)

# Direct command
./manage-app.sh start
```

### Checking Status
```bash
# Basic status
./manage-app.sh status

# Detailed status with metrics
./advanced-manage.sh status
```

### Viewing Logs
```bash
# Last 50 lines
./manage-app.sh logs

# Last 100 lines
./advanced-manage.sh logs 100

# Follow logs in real-time
./manage-app.sh logs -f
```

### Creating Backups
```bash
# Application backup
./advanced-manage.sh backup

# Database backup
./advanced-manage.sh db-backup
```

### Monitoring
```bash
# Show metrics
./advanced-manage.sh metrics

# Check health
curl http://localhost:8080/api/actuator/health
```

## 🚨 Troubleshooting

### Common Issues

#### 1. Permission Denied
```bash
chmod +x *.sh
```

#### 2. Port Already in Use
```bash
# Check what's using the port
lsof -i :8080

# Stop the conflicting process
./manage-app.sh stop
```

#### 3. JAR File Not Found
```bash
# Build the application first
mvn clean package
```

#### 4. Application Won't Start
```bash
# Check logs
./manage-app.sh logs

# Check status
./manage-app.sh status
```

### Debug Mode
For detailed debugging, you can modify the scripts to enable verbose output:

```bash
# Add to the start command
java -jar "$JAR_FILE" --debug > "$LOG_FILE" 2>&1 &
```

## 🔒 Security Considerations

- **PID Files**: Stored in current directory, ensure proper permissions
- **Log Files**: May contain sensitive information, secure appropriately
- **Backup Files**: Store in secure location with proper access controls
- **Database**: Backup files contain actual data, protect accordingly

## 📚 Additional Resources

- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Java Process Management](https://docs.oracle.com/javase/tutorial/essential/concurrency/procthread.html)

## 🤝 Contributing

To add new features to the management scripts:

1. **Basic Features**: Add to `manage-app.sh`
2. **Advanced Features**: Add to `advanced-manage.sh`
3. **Update Documentation**: Modify this README
4. **Test**: Verify on both Linux and Windows platforms

## 📄 License

These management scripts are part of the portfolio project and follow the same license terms.
