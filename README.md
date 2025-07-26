# Security Clearance Tracker API

A Spring Boot REST API for managing security clearance applications and tracking their progress.

## Technologies Used

- Java 17
- Spring Boot 3.2.2
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- Swagger/OpenAPI 3 for API documentation

## Features

- **Status History Management**: Track status changes and progress updates
- **Case History Management**: Manage case information and current status
- **RESTful API**: Full CRUD operations for all entities
- **API Documentation**: Interactive Swagger UI
- **CORS Support**: Configured for frontend integration
- **Data Validation**: Input validation using Bean Validation

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
- **H2 Console**: http://localhost:9090/api/h2-console (JDBC URL: `jdbc:h2:mem:testdb`, Username: `sa`, Password: `password`)

## API Endpoints

### Status History
- `GET /api/status-history` - Get all status history items
- `GET /api/status-history/{id}` - Get status history by ID
- `GET /api/status-history/status/{status}` - Get status history by status
- `GET /api/status-history/search?name={name}` - Search status history by name
- `POST /api/status-history` - Create new status history item
- `PUT /api/status-history/{id}` - Update status history item
- `DELETE /api/status-history/{id}` - Delete status history item

### Case History
- `GET /api/case-history` - Get all case history items
- `GET /api/case-history/{id}` - Get case history by ID
- `GET /api/case-history/case/{caseId}` - Get case history by case ID
- `GET /api/case-history/status/{status}` - Get case history by status
- `GET /api/case-history/active` - Get active cases
- `POST /api/case-history` - Create new case history item
- `PUT /api/case-history/{id}` - Update case history item
- `DELETE /api/case-history/{id}` - Delete case history item

## Sample Data

The application comes with sample data pre-loaded:

### Status History
- Application Submitted (completed)
- Initiated (completed)  
- Questionnaire Submitted (completed)
- Review & Investigation (in_progress)

### Case History
- SCT-2024-001 (In Progress)
- SCT-2024-002 (Under Review)
- SCT-2024-003 (Approved)
- SCT-2024-004 (Pending Documentation)
- SCT-2024-005 (Closed)

## Configuration

The application uses H2 in-memory database by default. Configuration can be modified in `src/main/resources/application.yml`.

### Database Configuration
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### CORS Configuration
- Allowed origins: `http://localhost:3000`, `http://localhost:3001`
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS

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

## Integration with Frontend

This API is designed to work with the Security Clearance Tracker frontend application. Make sure both applications are running:

- Frontend: http://localhost:3000 (or 3001)
- API: http://localhost:9090/api

The CORS configuration allows the frontend to make requests to this API.