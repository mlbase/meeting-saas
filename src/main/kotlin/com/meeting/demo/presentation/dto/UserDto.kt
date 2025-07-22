package com.meeting.demo.presentation.dto

import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.vo.Email
import java.io.Serializable

/**
 * Data Transfer Object for User entity.
 * This class is used to transfer user data between the presentation layer and the domain layer.
 */
data class UserDto(
    val id: Long? = null,
    val username: String,
    val password: String? = null, // Password is optional for DTOs, as it may not be needed in all contexts
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String
) : Serializable {
    companion object {
        /**
         * Convert a User domain model to a UserDto.
         *
         * @param user The User domain model to convert
         * @return The corresponding UserDto
         */
        fun fromUser(user: User): UserDto {
            return UserDto(
                id = user.id,
                username = user.username,
                email = user.email.value,
                firstName = user.firstName,
                lastName = user.lastName,
                fullName = "${user.firstName} ${user.lastName}"
            )
        }
        
        /**
         * Convert a list of User domain models to a list of UserDtos.
         *
         * @param users The list of User domain models to convert
         * @return The corresponding list of UserDtos
         */
        fun fromUsers(users: List<User>): List<UserDto> {
            return users.map { fromUser(it) }
        }
    }
    
    /**
     * Convert this UserDto to a User domain model.
     * Note: This method requires a password. For DTOs without passwords, 
     * consider using User.create() factory method instead.
     *
     * @return The corresponding User domain model
     */
    fun toUser(): User {
        val passwordValue = this.password ?: throw IllegalArgumentException("Password is required to create User domain model")
        return User.create(
            username = this.username,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            password = passwordValue
        )
    }
}