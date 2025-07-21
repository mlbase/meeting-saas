package com.meeting.demo.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(unique = true, nullable = false)
    val username: String,
    
    @Column(nullable = false)
    val password: String,
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(name = "first_name")
    val firstName: String,
    
    @Column(name = "last_name")
    val lastName: String,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    val roles: String? = null,
    
    @Column(name = "company_id")
    val companyId: Long? = null
)