package com.meeting.demo.infrastructure.repository

import com.meeting.demo.domain.model.User
import com.meeting.demo.domain.repository.UserRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * In-memory implementation of UserRepository.
 * This is a simple implementation for demonstration purposes.
 * In a real application, this would be replaced with a database-backed implementation.
 */
@Repository
class InMemoryUserRepository : UserRepository {
    
    private val users = ConcurrentHashMap<Long, User>()
    private val idCounter = AtomicLong(1)
    
    override fun findById(id: Long): User? {
        return users[id]
    }
    
    override fun findAll(): List<User> {
        return users.values.toList()
    }
    
    override fun save(user: User): User {
        val userId = user.id ?: idCounter.getAndIncrement()
        val savedUser = user.copy(id = userId)
        users[userId] = savedUser
        return savedUser
    }
    
    override fun deleteById(id: Long) {
        users.remove(id)
    }
}