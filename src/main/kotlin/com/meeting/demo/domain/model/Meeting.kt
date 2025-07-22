package com.meeting.demo.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "meetings")
class Meeting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(name = "host_user_id", nullable = false)
    val hostUserId: Long,
    
    @Column(name = "company_id", nullable = false)
    val companyId: Long,
    
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,
    
    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,
    
    @Enumerated(EnumType.STRING)
    val status: MeetingStatus = MeetingStatus.SCHEDULED
) : BaseEntity()

enum class MeetingStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}