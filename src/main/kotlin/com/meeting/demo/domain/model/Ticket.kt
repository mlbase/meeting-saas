package com.meeting.demo.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tickets")
data class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(name = "github_repository_id")
    val githubRepositoryId: String? = null,
    
    @Column(name = "user_id")
    val userId: Long? = null,
    
    @Column(name = "company_id", nullable = false)
    val companyId: Long,
    
    @Enumerated(EnumType.STRING)
    val status: TicketStatus = TicketStatus.OPEN,
    
    @Column(name = "is_closed")
    val isClosed: Boolean = false,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TicketStatus {
    OPEN, IN_PROGRESS, REVIEW, CLOSED
}