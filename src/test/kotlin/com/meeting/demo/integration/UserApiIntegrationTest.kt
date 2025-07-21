package com.meeting.demo.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

class UserApiIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

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
            "password" to "password123"
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
            .andExpect(jsonPath("$.message").value("Invalid email or role"))
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

        // Get all users
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].email").exists())
            .andExpect(jsonPath("$[1].email").exists())
    }

    @Test
    fun `GET api users by email - should return user when exists`() {
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

        // Find user by email
        mockMvc.perform(post("/api/users/check/findme@example.com"))
            .andExpect(status().isOk)
    }

    @Test
    fun `GET api users by email - should return 404 when user not found`() {
        mockMvc.perform(post("/api/users/check/notfound@example.com"))
            .andExpect(status().isBadRequest)
    }
}