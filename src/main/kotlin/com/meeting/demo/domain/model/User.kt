package com.meeting.demo.domain.model

/**
 * User entity representing a user in the system.
 */
data class User(
    val id: Long? = null,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String
)