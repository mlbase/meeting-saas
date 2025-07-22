package com.meeting.demo.domain.model

import jakarta.persistence.*

@Entity
@Table(name = "companies")
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(name = "is_active")
    val isActive: Boolean = true
) : BaseEntity()
