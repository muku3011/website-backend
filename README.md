# Portfolio Backend - Blog Management System

A Spring Boot backend application for managing blog posts with SQLite database integration.

## Features

- **Blog Management**: CRUD operations for blog posts
- **SQLite Database**: Lightweight, file-based database
- **RESTful API**: Clean API endpoints for frontend integration
- **Search Functionality**: Full-text search across blog content
- **Pagination**: Efficient data loading with pagination support
- **Featured Posts**: Support for highlighting important posts
- **View Tracking**: Track blog post views and popularity

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **SQLite Database**
- **Hibernate**
- **Maven**

## API Endpoints

### Blog Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/blogs` | Get all published blogs |
| GET | `/api/blogs/page` | Get blogs with pagination |
| GET | `/api/blogs/{id}` | Get blog by ID |
| GET | `/api/blogs/slug/{slug}` | Get published blog by slug |
| GET | `/api/blogs/featured` | Get featured blogs |
| GET | `/api/blogs/search` | Search blogs |
| GET | `/api/blogs/recent` | Get recent blogs |
| GET | `/api/blogs/popular` | Get popular blogs |
| GET | `/api/blogs/stats` | Get blog statistics |
| POST | `/api/blogs` | Create new blog (Admin) |
| PUT | `/api/blogs/{id}` | Update blog (Admin) |
| DELETE | `/api/blogs/{id}` | Delete blog (Admin) |

### Query Parameters

- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `q`: Search query for search endpoint
- `limit`: Limit for recent/popular posts (default: 5)

## Database Schema

### Blogs Table

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| title | VARCHAR(200) | Blog post title |
| content | TEXT | Full blog content |
| excerpt | VARCHAR(500) | Short description |
| author | VARCHAR(100) | Author name |
| featured_image_url | VARCHAR | Featured image URL |
| slug | VARCHAR | URL-friendly identifier |
| status | VARCHAR | Blog status (DRAFT, PUBLISHED, ARCHIVED) |
| view_count | BIGINT | Number of views |
| is_featured | BOOLEAN | Featured post flag |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp |
| published_at | TIMESTAMP | Publication timestamp |

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd portfolio-backend
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api`
   - Health Check: `http://localhost:8080/api/actuator/health`

### Database

The application uses SQLite database (`portfolio.db`) which will be created automatically on first run. The database file will be located in the project root directory.

Sample blog data will be automatically populated on first startup.

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:sqlite:portfolio.db
    driver-class-name: org.sqlite.JDBC
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### CORS Configuration

The application includes CORS configuration to allow frontend integration:

```java
@CrossOrigin(origins = "*")
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/mukeshjoshi/portfolio/
│   │       ├── PortfolioBackendApplication.java
│   │       ├── config/
│   │       │   └── DataInitializer.java
│   │       ├── controller/
│   │       │   └── BlogController.java
│   │       ├── dto/
│   │       │   ├── BlogDto.java
│   │       │   └── BlogSummaryDto.java
│   │       ├── entity/
│   │       │   ├── Blog.java
│   │       │   └── BlogStatus.java
│   │       ├── repository/
│   │       │   └── BlogRepository.java
│   │       └── service/
│   │           └── BlogService.java
│   └── resources/
│       └── application.yml
└── test/
```

### Adding New Features

1. **Entity**: Create entity classes in `entity` package
2. **Repository**: Create repository interfaces in `repository` package
3. **Service**: Create service classes in `service` package
4. **Controller**: Create REST controllers in `controller` package
5. **DTO**: Create data transfer objects in `dto` package

### Testing

Run tests with:
```bash
mvn test
```

## Deployment

### JAR Deployment

1. **Build JAR**
   ```bash
   mvn clean package
   ```

2. **Run JAR**
   ```bash
   java -jar target/portfolio-backend-0.0.1-SNAPSHOT.jar
   ```

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/portfolio-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t portfolio-backend .
docker run -p 8080:8080 portfolio-backend
```

## API Examples

### Get All Blogs
```bash
curl -X GET "http://localhost:8080/api/blogs"
```

### Get Blogs with Pagination
```bash
curl -X GET "http://localhost:8080/api/blogs/page?page=0&size=5"
```

### Search Blogs
```bash
curl -X GET "http://localhost:8080/api/blogs/search?q=java&page=0&size=10"
```

### Get Blog by Slug
```bash
curl -X GET "http://localhost:8080/api/blogs/slug/enterprise-architecture-best-practices"
```

### Create New Blog
```bash
curl -X POST "http://localhost:8080/api/blogs" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Blog Post",
    "content": "Full blog content here...",
    "excerpt": "Short description",
    "author": "Mukesh Joshi",
    "status": "PUBLISHED",
    "isFeatured": false
  }'
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   - Change port in `application.yml`
   - Kill process using port 8080

2. **Database Connection Issues**
   - Ensure SQLite JDBC driver is available
   - Check file permissions for database file

3. **CORS Issues**
   - Verify CORS configuration
   - Check frontend URL configuration

### Logs

Enable debug logging by adding to `application.yml`:

```yaml
logging:
  level:
    com.mukeshjoshi: DEBUG
    org.springframework.web: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
