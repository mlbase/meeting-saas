# API Specification

## User API
1. base domain: /api/users
2. methods:
    - POST /api/users: Create a new user
    - POST /api/users/login: Authenticate user
    - POST /api/users/check-email: Check if email is already in use
    - POST /api/users/reset-password: Reset user password
    - PUT /api/users/{id}: Update user by ID
    - GET /api/users/{id}: Get user by ID
    