package com.meeting.demo.domain.service

import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.repository.UserRepository
import com.meeting.demo.presentation.dto.UserDto

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for handling business logic related to User entities.
 */
@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun saveUser(user: User): Long {
        // Here you can add business logic such as validation, hashing passwords, etc.
        userRepository.save(user)
        return user.id ?: throw IllegalArgumentException("User ID cannot be null after saving")
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserDto? {
        // Fetch user by ID, can add additional business logic if needed
        val user: User? = userRepository.findById(id)
        return user?.let { UserDto.fromUser(it) }
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserDto> {
        // Fetch all users, can add additional business logic if needed
        val userList: List<User> = userRepository.findAll()
        return UserDto.fromUsers(userList)
    }

    @Transactional(readOnly = true)
    fun checkEmailExists(email: String): Boolean {
        // Check if a user with the given email exists
        return userRepository.findByEmail(email) != null
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): UserDto? {
        // Here you can add business logic for login, such as checking password validity
        val user: User? = userRepository.findByEmail(email)
        return if (user != null && user.verifyPassword(password) && user.isActive) {
            UserDto.fromUser(user)
        } else {
            null // or throw an exception if preferred
        }
    }
    
    @Transactional
    fun deleteAllUsers() {
        userRepository.deleteAll()
    }
}
