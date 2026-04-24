package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.InvalidStateTransitionException
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
    var status: MeetingStatus = MeetingStatus.SCHEDULED,

    @OneToMany(mappedBy = "meetingId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val participants: MutableList<Participant> = mutableListOf()
) : BaseEntity() {

    fun drop() {
        if (status != MeetingStatus.SCHEDULED)
            throw IllegalStateException("SCHEDULED 상태의 미팅만 드롭할 수 있습니다.")
        status = MeetingStatus.DROPPED
    }

    fun start() {
        if (status != MeetingStatus.SCHEDULED)
            throw IllegalStateException("SCHEDULED 상태의 미팅만 시작할 수 있습니다.")
        status = MeetingStatus.IN_PROGRESS
    }

    fun submit() {
        if (status != MeetingStatus.IN_PROGRESS)
            throw IllegalStateException("IN_PROGRESS 상태의 미팅만 제출할 수 있습니다.")
        status = MeetingStatus.SUBMITTED
    }

    fun startBatchProcessing() {
        if (status != MeetingStatus.SUBMITTED)
            throw IllegalStateException("SUBMITTED 상태의 미팅만 배치 처리를 시작할 수 있습니다.")
        status = MeetingStatus.BATCH_PROCESSING
    }

    fun complete() {
        if (status != MeetingStatus.BATCH_PROCESSING)
            throw IllegalStateException("BATCH_PROCESSING 상태의 미팅만 완료할 수 있습니다.")
        status = MeetingStatus.COMPLETED
    }

    fun addParticipant(userId: Long) {
        if (hasParticipant(userId))
            throw IllegalArgumentException("이미 추가된 참가자입니다.")
        participants.add(Participant(userId = userId, meetingId = id ?: 0L))
    }

    fun removeParticipant(userId: Long) {
        val participant = participants.find { it.userId == userId }
            ?: throw IllegalArgumentException("존재하지 않는 참가자입니다.")
        participants.remove(participant)
    }

    fun hasParticipant(userId: Long): Boolean = participants.any { it.userId == userId }

    fun isCompleted(): Boolean = status == MeetingStatus.COMPLETED
}

enum class MeetingStatus {
    SCHEDULED, IN_PROGRESS, SUBMITTED, BATCH_PROCESSING, COMPLETED, DROPPED
}