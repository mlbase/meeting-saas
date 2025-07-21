package com.meeting.demo.controller

import com.meeting.demo.model.Participant
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController {

    // In-memory storage for demo purposes
    private val users = mutableListOf<Participant>()

    @PostMapping
    fun createUser(@RequestBody userData: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        try {
            // Create new participant from registration data
            val participant = Participant(
                name = userData.name,
                email = userData.email,
                role = userData.role,
                department = userData.department
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
            users.add(participant)

            return ResponseEntity.ok(
                UserRegistrationResponse(
                    success = true,
                    message = "User registered successfully",
                    userId = participant.email // Using email as ID for now
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
    fun getAllUsers(): ResponseEntity<List<Participant>> {
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<Participant> {
        val user = users.find { it.email == email }
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
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
                    userId = user.email // Using email as ID for now
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
    val userId: String?
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserLoginResponse(
    val success: Boolean,
    val message: String,
    val userId: String?
)