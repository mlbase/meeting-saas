package com.meeting.demo.domain.model

import jakarta.persistence.*

@Entity
@Table(name = "tickets")
class Ticket(
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
    val isClosed: Boolean = false
) : BaseEntity()

enum class TicketStatus {
    OPEN, IN_PROGRESS, REVIEW, CLOSED
}