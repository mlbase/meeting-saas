package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.User

/**
 * Repository interface for User entity.
 * This interface defines methods for accessing and manipulating User data.
 */
interface UserRepository {
    /**
     * Find a user by their ID.
     *
     * @param id The ID of the user to find
     * @return The user if found, null otherwise
     */
    fun findById(id: Long): User?
    
    /**
     * Find all users in the system.
     *
     * @return A list of all users
     */
    fun findAll(): List<User>
    
    /**
     * Save a user to the repository.
     *
     * @param user The user to save
     * @return The saved user with updated ID if it was a new user
     */
    fun save(user: User): User
    
    /**
     * Delete a user from the repository.
     *
     * @param id The ID of the user to delete
     */
    fun deleteById(id: Long)
}