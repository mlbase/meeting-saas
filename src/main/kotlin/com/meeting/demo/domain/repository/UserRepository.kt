package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.User


interface UserRepository {

    fun findById(id: Long): User?

    fun findByEmail(email: String): User?

    fun findAll(): List<User>

    fun save(user: User): User

    fun deleteById(id: Long)
    
    fun deleteAll()
}