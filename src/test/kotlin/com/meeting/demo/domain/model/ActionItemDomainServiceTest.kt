package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.InvalidStateTransitionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ActionItemDomainServiceTest {

    private val actionItemDomainService = ActionItemDomainService()

    private fun 액션아이템_생성(
        status: ActionItemStatus = ActionItemStatus.CANDIDATE
    ) = ActionItem(
        title = "테스트 액션아이템",
        meetingId = 1L,
        epicStoryId = 1L,
        proposerUserId = 1L,
        status = status
    )

    // ==============================
    // pickUp — 유저 상태 검증 (cross-BC rule)
    // ==============================

    @Test
    fun `PLANNING 상태의 유저가 CANDIDATE 액션아이템을 픽업할 수 있다`() {
        val actionItem = 액션아이템_생성()

        actionItemDomainService.pickUp(
            actionItem = actionItem,
            assigneeUserId = 2L,
            userStatus = UserStatus.PLANNING
        )

        assertEquals(ActionItemStatus.PICKED_UP, actionItem.status)
        assertEquals(2L, actionItem.assigneeUserId)
    }

    @Test
    fun `AVAILABLE 상태의 유저는 액션아이템을 픽업할 수 없다`() {
        val actionItem = 액션아이템_생성()

        assertThrows<InvalidStateTransitionException> {
            actionItemDomainService.pickUp(
                actionItem = actionItem,
                assigneeUserId = 2L,
                userStatus = UserStatus.AVAILABLE
            )
        }
    }

    @Test
    fun `WORKING 상태의 유저는 액션아이템을 픽업할 수 없다`() {
        val actionItem = 액션아이템_생성()

        assertThrows<InvalidStateTransitionException> {
            actionItemDomainService.pickUp(
                actionItem = actionItem,
                assigneeUserId = 2L,
                userStatus = UserStatus.WORKING
            )
        }
    }
}