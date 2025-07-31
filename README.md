# Security Clearance Tracker API

A Spring Boot REST API for managing security clearance applications and tracking their progress.

## Technologies Used

- Java 17
- Spring Boot 3.2.2
- Maven
- Swagger/OpenAPI 3 for API documentation



## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Application

1. Navigate to the project directory:
   ```bash
   cd security-clearance-tracker-api
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. The API will be available at: `http://localhost:9090/api`

### API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:9090/api/swagger-ui.html
- **API Docs**: http://localhost:9090/api/api-docs

## API Endpoints

### Status History
- `GET /api/status-history` - Get all status history items


### Case History
- `GET /api/case-history` - Get all case history items





## Configuration


## Development

### Building the Project
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Creating a JAR
```bash
mvn clean package
```

The JAR file will be created in the `target/` directory.

 Final Docker Usage:
  # Build the image
  docker build -t security-clearance-tracker .

  # Run the container
  docker run -p 8080:80 security-clearance-tracker

  # Access at http://localhost:8080

