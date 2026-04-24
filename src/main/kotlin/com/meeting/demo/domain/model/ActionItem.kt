package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.InvalidStateTransitionException
import com.meeting.demo.domain.exception.UnauthorizedActionException
import jakarta.persistence.*

@Entity
@Table(name = "action_items")
class ActionItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(name = "meeting_id", nullable = false)
    val meetingId: Long,

    @Column(name = "epic_story_id")
    val epicStoryId: Long? = null,

    @Column(name = "ticket_id")
    var ticketId: Long? = null,

    @Column(name = "proposer_user_id", nullable = false)
    val proposerUserId: Long,

    @Column(name = "assignee_user_id")
    var assigneeUserId: Long? = null,

    @Enumerated(EnumType.STRING)
    var status: ActionItemStatus = ActionItemStatus.CANDIDATE,

    @Enumerated(EnumType.STRING)
    var priority: ActionItemPriority = ActionItemPriority.MEDIUM,

    @Column(name = "is_confirmed")
    var isConfirmed: Boolean = false
) : BaseEntity() {

    fun pickUp(assigneeUserId: Long) {
        if (status != ActionItemStatus.CANDIDATE)
            throw InvalidStateTransitionException("CANDIDATE 상태의 액션아이템만 픽업할 수 있습니다.")
        this.assigneeUserId = assigneeUserId
        status = ActionItemStatus.PICKED_UP
    }

    fun startProgress(requestUserId: Long) {
        validateAssignee(requestUserId)
        if (status != ActionItemStatus.PICKED_UP)
            throw InvalidStateTransitionException("PICKED_UP 상태의 액션아이템만 진행 시작할 수 있습니다.")
        status = ActionItemStatus.IN_PROGRESS
    }

    fun submitForDeploy(requestUserId: Long) {
        validateAssignee(requestUserId)
        if (status != ActionItemStatus.IN_PROGRESS)
            throw InvalidStateTransitionException("IN_PROGRESS 상태의 액션아이템만 배포 대기로 전환할 수 있습니다.")
        status = ActionItemStatus.DEPLOY_WAITING
    }

    fun completeDeploy(requestUserId: Long) {
        validateAssignee(requestUserId)
        if (status != ActionItemStatus.DEPLOY_WAITING)
            throw InvalidStateTransitionException("DEPLOY_WAITING 상태의 액션아이템만 QA로 전환할 수 있습니다.")
        status = ActionItemStatus.QA
    }

    fun completeQA(requestUserId: Long) {
        validateAssignee(requestUserId)
        if (status != ActionItemStatus.QA)
            throw InvalidStateTransitionException("QA 상태의 액션아이템만 RELEASED로 전환할 수 있습니다.")
        status = ActionItemStatus.RELEASED
    }

    fun changePriority(requestUserId: Long, priority: ActionItemPriority) {
        if (status == ActionItemStatus.RELEASED)
            throw InvalidStateTransitionException("RELEASED 상태의 액션아이템은 우선순위를 변경할 수 없습니다.")
        if (proposerUserId != requestUserId)
            throw UnauthorizedActionException("입안자만 우선순위를 변경할 수 있습니다.")
        this.priority = priority
    }

    fun confirm() {
        if (isConfirmed)
            throw InvalidStateTransitionException("이미 확인된 액션아이템입니다.")
        isConfirmed = true
    }

    private fun validateAssignee(requestUserId: Long) {
        if (assigneeUserId != requestUserId)
            throw UnauthorizedActionException("담당자만 상태를 변경할 수 있습니다.")
    }
}

enum class ActionItemStatus {
    CANDIDATE, PICKED_UP, IN_PROGRESS, DEPLOY_WAITING, QA, RELEASED
}

enum class ActionItemPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}