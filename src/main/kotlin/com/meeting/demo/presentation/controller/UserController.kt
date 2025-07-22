package com.meeting.demo.presentation.controller

import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.service.UserService
import com.meeting.demo.domain.vo.Email
import com.meeting.demo.domain.vo.Password
import com.meeting.demo.presentation.dto.UserDto
import com.meeting.demo.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.lang.Math.random

@RestController
@RequestMapping("/api/users")
class UserController(
    private val service: UserService,
    private val sessionService: SessionService
) {

    @PostMapping
    fun createUser(@RequestBody userData: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        try {
            // Check if user already exists using service
            if (service.checkEmailExists(userData.email)) {
                return ResponseEntity.badRequest().body(
                    UserRegistrationResponse(
                        success = false,
                        message = "User with this email already exists",
                        userId = null
                    )
                )
            }

            // Create new User from registration data using domain factory method
            val createdUser = User.create(
                username = userData.name,
                email = userData.email,
                firstName = userData.name.split(" ").getOrNull(0) ?: userData.name,
                lastName = userData.name.split(" ").getOrNull(1) ?: "",
                password = userData.password,
                roles = "ROLE_${userData.role.uppercase()}"
            )

            // Add user to storage
            service.saveUser(createdUser)

            return ResponseEntity.ok(
                UserRegistrationResponse(
                    success = true,
                    message = "User registered successfully",
                    userId = createdUser.id
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
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val userList: List<UserDto> = service.getAllUsers()
        return ResponseEntity.ok(userList)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDto> {
        val user: UserDto? = service.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/check/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<Void> {
        val isEmailExists: Boolean = service.checkEmailExists(email)
        return if (!isEmailExists) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun loginUser(
        @RequestBody loginData: UserLoginRequest,
        request: HttpServletRequest
    ): ResponseEntity<UserLoginResponse> {
        val user: UserDto? = service.login(loginData.email, loginData.password)
        return if (user != null) {
            // Create Redis session for authenticated user
            val sessionId = sessionService.createUserSession(request, user)
            
            ResponseEntity.ok(
                UserLoginResponse(
                    success = true,
                    message = "Login successful",
                    userId = user.id,
                    sessionId = sessionId
                )
            )
        } else {
            ResponseEntity.status(401).body(
                UserLoginResponse(
                    success = false,
                    message = "Invalid email or password",
                    userId = null,
                    sessionId = null
                )
            )
        }
    }

    @PostMapping("/logout")
    fun logoutUser(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val wasLoggedIn = sessionService.invalidateSession(request)
        
        return if (wasLoggedIn) {
            ResponseEntity.ok(
                mapOf(
                    "success" to true,
                    "message" to "Logout successful"
                )
            )
        } else {
            ResponseEntity.badRequest().body(
                mapOf(
                    "success" to false,
                    "message" to "No active session found"
                )
            )
        }
    }

    @GetMapping("/session")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val user = sessionService.getUserFromSession(request)
        
        return if (user != null) {
            val loginTime = sessionService.getLoginTime(request)
            ResponseEntity.ok(
                mapOf<String, Any>(
                    "authenticated" to true,
                    "user" to user,
                    "loginTime" to (loginTime ?: 0L)
                )
            )
        } else {
            ResponseEntity.status(401).body(
                mapOf<String, Any>(
                    "authenticated" to false,
                    "message" to "No active session"
                )
            )
        }
    }

    @DeleteMapping("")
    fun deleteAllUsers(): ResponseEntity<String> {
        service.deleteAllUsers()
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
    val sessionId: String? = null
)