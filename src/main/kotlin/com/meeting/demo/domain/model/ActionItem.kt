package com.meeting.demo.domain.model

import jakarta.persistence.*

@Entity
@Table(name = "action_items")
class ActionItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(name = "ticket_id")
    val ticketId: Long? = null,
    
    @Column(name = "meeting_id", nullable = false)
    val meetingId: Long,
    
    @Column(name = "is_confirmed")
    val isConfirmed: Boolean = false,
    
    @Column(name = "assignee_user_id")
    val assigneeUserId: Long? = null
) : BaseEntity()