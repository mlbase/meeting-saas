package com.meeting.demo.controller

import com.meeting.demo.model.UserData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.lang.Math.random

@RestController
@RequestMapping("/api/users")
class UserController {

    // In-memory storage for demo purposes
    private val users = mutableListOf<UserData>()

    @PostMapping
    fun createUser(@RequestBody userData: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        try {
            // Create new UserData from registration data
            val createdUser = UserData(
                id = random().toLong(),
                username = userData.name,
                email = userData.email,
                firstName = userData.name.split(" ").getOrNull(0) ?: userData.name,
                lastName = userData.name.split(" ").getOrNull(1) ?: "",
                isActive = true,
                password = userData.password,
                roles = listOf(userData.role)
            )

            // Check if user already exists
            if (users.any { it.email == userData.email }) {
                return ResponseEntity.badRequest().body(
                    UserRegistrationResponse(
                        success = false,
                        message = "User with this email already exists",
                        userId = null
                    )
                )
            }

            // Add user to storage
            users.add(createdUser)

            return ResponseEntity.ok(
                UserRegistrationResponse(
                    success = true,
                    message = "User registered successfully",
                    userId = createdUser.id // Using email as ID for now
                )
            )

        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(
                UserRegistrationResponse(
                    success = false,
                    message = "Registration failed: ${e.message}",
                    userId = null
                )
            )
        }
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserData>> {
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserData> {
        val user = users.find { it.id == id }
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/check/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserData> {
        val user = users.find { it.email == email }
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody loginData: UserLoginRequest): ResponseEntity<UserLoginResponse> {
        val user = users.find { it.email == loginData.email && it.password == loginData.password }
        return if (user != null) {
            ResponseEntity.ok(
                UserLoginResponse(
                    success = true,
                    message = "Login successful",
                    userId = user.id // Using email as ID for now
                )
            )
        } else {
            ResponseEntity.status(401).body(
                UserLoginResponse(
                    success = false,
                    message = "Invalid email or role",
                    userId = null
                )
            )
        }
    }

    @DeleteMapping("")
    fun deleteAllUsers(): ResponseEntity<String> {
        users.clear()
        return ResponseEntity.ok("All users deleted successfully")
    }
}

data class UserRegistrationRequest(
    val name: String,
    val email: String,
    val department: String,
    val role: String,
    val password: String
)

data class UserRegistrationResponse(
    val success: Boolean,
    val message: String,
    val userId: Long? = null
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserLoginResponse(
    val success: Boolean,
    val message: String,
    val userId: Long?,
)