package com.meeting.demo.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserApiIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `POST api users - should create new user successfully`() {
        val userRequest = mapOf(
            "name" to "John Doe",
            "email" to "john.doe@example.com",
            "department" to "Engineering",
            "role" to "Developer",
            "password" to "Password123!"
        )

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.userId").isNumber())
    }

    @Test
    fun `POST api users - should fail when user already exists`() {
        // Create existing user
        val userRequest = mapOf(
            "name" to "Jane Smith",
            "email" to "jane.smith@example.com",
            "department" to "Marketing",
            "role" to "Manager",
            "password" to "password123"
        )

        // First request should succeed
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )
            .andExpect(status().isOk)

        // Second request should fail
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User with this email already exists"))
    }

    @Test
    fun `POST api users login - should authenticate user successfully`() {
        // First create a user
        val userRequest = mapOf(
            "name" to "Test User",
            "email" to "test@example.com",
            "department" to "IT",
            "role" to "Developer",
            "password" to "password123"
        )

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )

        // Then try to login
        val loginRequest = mapOf(
            "email" to "test@example.com",
            "password" to "password123"
        )

        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.userId").isNumber())
            .andExpect(jsonPath("$.sessionId").isString)
    }

    @Test
    fun `POST api users login - should fail with invalid credentials`() {
        val loginRequest = mapOf(
            "email" to "nonexistent@example.com",
            "password" to "wrongpassword"
        )

        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Invalid email or password"))
    }

    @Test
    fun `GET api users - should return all users`() {
        // Create test users
        val user1Request = mapOf(
            "name" to "User One",
            "email" to "user1@example.com",
            "department" to "Engineering",
            "role" to "Developer",
            "password" to "password1"
        )

        val user2Request = mapOf(
            "name" to "User Two",
            "email" to "user2@example.com",
            "department" to "Marketing",
            "role" to "Manager",
            "password" to "password2"
        )

        mockMvc.perform(delete("/api/users"))
            .andExpect(status().isOk)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1Request))
        )

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2Request))
        )

        // Get all users - should have exactly 2 after delete and create
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].email").exists())
            .andExpect(jsonPath("$[1].email").exists())
            .andExpect(jsonPath("$[0].username").exists())
            .andExpect(jsonPath("$[1].username").exists())
    }

    @Test
    fun `GET api users by email - should return 400 when email exists`() {
        // Create user first
        val userRequest = mapOf(
            "name" to "Find Me",
            "email" to "findme@example.com",
            "department" to "Sales",
            "role" to "Representative",
            "password" to "password"
        )

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )

        // Check email availability - should return 400 when email is taken
        mockMvc.perform(post("/api/users/check/findme@example.com"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET api users by email - should return 200 when email available`() {
        // Check email availability - should return 200 when email is available
        mockMvc.perform(post("/api/users/check/notfound@example.com"))
            .andExpect(status().isOk)
    }

    @Test
    fun `POST api users logout - should logout user successfully`() {
        // First create and login a user
        val userRequest = mapOf(
            "name" to "Logout Test User",
            "email" to "logout@example.com",
            "department" to "IT",
            "role" to "Developer",
            "password" to "password123"
        )

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )

        val loginRequest = mapOf(
            "email" to "logout@example.com",
            "password" to "password123"
        )

        val loginResult = mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        // Extract session ID from the X-Auth-Token header
        val sessionId = loginResult.response.getHeader("X-Auth-Token")

        // Perform logout with the same session ID via header
        mockMvc.perform(
            post("/api/users/logout")
                .header("X-Auth-Token", sessionId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Logout successful"))
    }

    @Test
    fun `GET api users session - should return current user session`() {
        // First create and login a user
        val userRequest = mapOf(
            "name" to "Session Test User",
            "email" to "session@example.com",
            "department" to "IT",
            "role" to "Developer",
            "password" to "password123"
        )

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )

        val loginRequest = mapOf(
            "email" to "session@example.com",
            "password" to "password123"
        )

        val loginResult = mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        // Extract session ID from the X-Auth-Token header
        val sessionId = loginResult.response.getHeader("X-Auth-Token")

        // Check current session with the session ID via header
        mockMvc.perform(
            get("/api/users/session")
                .header("X-Auth-Token", sessionId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.authenticated").value(true))
            .andExpect(jsonPath("$.user").exists())
            .andExpect(jsonPath("$.user.email").value("session@example.com"))
            .andExpect(jsonPath("$.loginTime").isNumber)
    }

    @Test
    fun `GET api users session - should return 401 when no session`() {
        // Check session without login
        mockMvc.perform(get("/api/users/session"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.authenticated").value(false))
            .andExpect(jsonPath("$.message").value("No active session"))
    }
}