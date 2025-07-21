package com.meeting.demo.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "participants")
data class Participant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    
    @Column(name = "meeting_id", nullable = false)
    val meetingId: Long,
    
    @Column(name = "joined_at")
    val joinedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "left_at")
    val leftAt: LocalDateTime? = null
)