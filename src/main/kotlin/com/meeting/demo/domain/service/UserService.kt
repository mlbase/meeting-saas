package com.meeting.demo.domain.service

import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.repository.UserRepository
import org.springframework.stereotype.Service

/**
 * Service class for handling business logic related to User entities.
 */
@Service
class UserService(private val userRepository: UserRepository) {

    /**
     * Get a user by their ID.
     *
     * @param id The ID of the user to retrieve
     * @return The user if found, null otherwise
     */
    fun getUserById(id: Long): User? {
        return userRepository.findById(id)
    }

    /**
     * Get all users in the system.
     *
     * @return A list of all users
     */
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    /**
     * Create a new user or update an existing one.
     *
     * @param user The user to create or update
     * @return The saved user with updated ID if it was a new user
     */
    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    /**
     * Delete a user by their ID.
     *
     * @param id The ID of the user to delete
     */
    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }
}
