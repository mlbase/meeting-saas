# Layered Architecture Skeleton

This project demonstrates a layered architecture for a Spring Boot application. The architecture is organized into the following layers:

## Domain Layer
The domain layer contains the core business logic and entities of the application.

### Model
- `User`: Represents a user entity in the system.

### Repository Interfaces
- `UserRepository`: Defines methods for accessing and manipulating User data.

### Services
- `UserService`: Handles business logic related to User entities.

## Infrastructure Layer
The infrastructure layer provides implementations of the repository interfaces defined in the domain layer.

### Repository Implementations
- `InMemoryUserRepository`: An in-memory implementation of the UserRepository interface.

## Presentation Layer
The presentation layer handles HTTP requests and responses, and converts between domain models and DTOs.

### Controllers
- `UserController`: REST controller for handling User-related HTTP requests.

### DTOs (Data Transfer Objects)
- `UserDto`: Data Transfer Object for User entity, used to transfer data between the presentation layer and the domain layer.

## Layer Interactions
1. The presentation layer (controllers) receives HTTP requests and converts DTOs to domain models.
2. The domain layer (services) processes business logic using domain models.
3. The domain layer (repositories) defines interfaces for data access.
4. The infrastructure layer implements the repository interfaces to provide data access.
5. The presentation layer converts domain models back to DTOs for HTTP responses.

## Benefits of Layered Architecture
- **Separation of Concerns**: Each layer has a specific responsibility.
- **Maintainability**: Changes in one layer don't affect other layers as long as the interfaces remain the same.
- **Testability**: Each layer can be tested independently.
- **Flexibility**: Different implementations of a layer can be swapped without affecting other layers.

## GitHub Actions and Secrets

This project uses GitHub Actions for CI/CD. The workflow is defined in `.github/workflows/main.yml`.

### Setting Up GitHub Secrets

To use the CI/CD pipeline, you need to set up the following GitHub Secrets in your repository:

1. Go to your GitHub repository
2. Click on "Settings" > "Secrets and variables" > "Actions"
3. Click on "New repository secret"
4. Add the following secrets:

| Secret Name | Description |
|-------------|-------------|
| `API_KEY` | API key for external services |
| `DATABASE_PASSWORD` | Password for database access |
| `DOCKER_USERNAME` | Docker Hub username for publishing images |
| `DOCKER_PASSWORD` | Docker Hub password or access token |
| `REDIS_PASSWORD` | Password for Redis authentication |

### How Secrets are Used

GitHub Secrets are encrypted environment variables that are only exposed to selected GitHub Actions workflows. In our workflow, secrets are used for:

- Authenticating with external services
- Connecting to databases
- Logging in to Docker Hub for image publishing
- Securing Redis with password authentication

Secrets are referenced in the workflow using the syntax: `${{ secrets.SECRET_NAME }}`

**Important**: Never print secrets in logs or expose them in any way in your workflows.

### Local Development with Secrets

For local development, you can create a `.env` file in the project root with the same secrets used in GitHub Actions:

```
API_KEY=your_api_key_here
DATABASE_PASSWORD=your_database_password_here
REDIS_PASSWORD=your_redis_password_here
```

This file should be added to `.gitignore` to prevent it from being committed to the repository.

To use the `.env` file with Docker Compose:

```bash
# Load environment variables from .env file
docker-compose --env-file .env -f compose.yaml up -d
```

For running the application locally without Docker:

```bash
# Export environment variables (Linux/macOS)
export $(cat .env | xargs)

# Or for Windows PowerShell
Get-Content .env | ForEach-Object { $var = $_.Split('=', 2); if ($var[0] -and $var[1]) { [Environment]::SetEnvironmentVariable($var[0], $var[1]) } }

# Then run the application
./gradlew bootRun
```
