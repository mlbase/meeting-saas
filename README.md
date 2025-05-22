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