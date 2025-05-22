package com.meeting.demo.presentation.controller

import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.service.UserService
import com.meeting.demo.presentation.dto.UserDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for handling User-related HTTP requests.
 */
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    /**
     * Get a user by their ID.
     *
     * @param id The ID of the user to retrieve
     * @return The user DTO if found, or a 404 response if not found
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDto> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(UserDto.fromUser(user))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Get all users.
     *
     * @return A list of all user DTOs
     */
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(UserDto.fromUsers(users))
    }

    /**
     * Create a new user.
     *
     * @param userDto The user DTO to create
     * @return The created user DTO with a 201 Created status
     */
    @PostMapping
    fun createUser(@RequestBody userDto: UserDto): ResponseEntity<UserDto> {
        val savedUser = userService.saveUser(userDto.toUser())
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.fromUser(savedUser))
    }

    /**
     * Update an existing user.
     *
     * @param id The ID of the user to update
     * @param userDto The updated user DTO data
     * @return The updated user DTO, or a 404 response if the user was not found
     */
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody userDto: UserDto): ResponseEntity<UserDto> {
        // Check if user exists
        if (userService.getUserById(id) == null) {
            return ResponseEntity.notFound().build()
        }

        // Ensure the ID in the path matches the ID in the user object
        val userToUpdate = userDto.toUser().copy(id = id)
        val updatedUser = userService.saveUser(userToUpdate)
        return ResponseEntity.ok(UserDto.fromUser(updatedUser))
    }

    /**
     * Delete a user.
     *
     * @param id The ID of the user to delete
     * @return A 204 No Content response
     */
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
