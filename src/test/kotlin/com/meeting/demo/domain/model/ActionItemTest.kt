package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.InvalidStateTransitionException
import com.meeting.demo.domain.exception.UnauthorizedActionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ActionItemTest {

    private fun 액션아이템_생성(
        proposerUserId: Long = 1L,
        assigneeUserId: Long? = null,
        status: ActionItemStatus = ActionItemStatus.CANDIDATE,
        priority: ActionItemPriority = ActionItemPriority.MEDIUM
    ) = ActionItem(
        title = "테스트 액션아이템",
        meetingId = 1L,
        epicStoryId = 1L,
        proposerUserId = proposerUserId,
        assigneeUserId = assigneeUserId,
        status = status,
        priority = priority
    )


    // ==============================
    // 기본값
    // ==============================

    @Test
    fun `액션아이템의 기본 상태는 CANDIDATE이다`() {
        val actionItem = 액션아이템_생성()
        assertEquals(ActionItemStatus.CANDIDATE, actionItem.status)
    }

    @Test
    fun `액션아이템의 기본 확인 여부는 false이다`() {
        val actionItem = 액션아이템_생성()
        assertFalse(actionItem.isConfirmed)
    }

    // ==============================
    // pickUp — aggregate 자체 불변식만
    // ==============================

    @Test
    fun `CANDIDATE 액션아이템을 픽업하면 PICKED_UP 상태가 된다`() {
        val actionItem = 액션아이템_생성()

        actionItem.pickUp(assigneeUserId = 2L)

        assertEquals(ActionItemStatus.PICKED_UP, actionItem.status)
        assertEquals(2L, actionItem.assigneeUserId)
    }

    @Test
    fun `CANDIDATE 상태가 아닌 액션아이템은 픽업할 수 없다`() {
        val actionItem = 액션아이템_생성(status = ActionItemStatus.PICKED_UP)

        assertThrows<InvalidStateTransitionException> {
            actionItem.pickUp(assigneeUserId = 2L)
        }
    }

    // ==============================
    // 상태 전이 — 정상 흐름
    // ==============================

    @Test
    fun `담당자는 PICKED_UP 액션아이템을 IN_PROGRESS로 전환할 수 있다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.PICKED_UP)

        actionItem.startProgress(requestUserId = 2L)

        assertEquals(ActionItemStatus.IN_PROGRESS, actionItem.status)
    }

    @Test
    fun `담당자는 IN_PROGRESS 액션아이템을 배포 대기로 전환할 수 있다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.IN_PROGRESS)

        actionItem.submitForDeploy(requestUserId = 2L)

        assertEquals(ActionItemStatus.DEPLOY_WAITING, actionItem.status)
    }

    @Test
    fun `담당자는 배포 대기 액션아이템을 QA로 전환할 수 있다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.DEPLOY_WAITING)

        actionItem.completeDeploy(requestUserId = 2L)

        assertEquals(ActionItemStatus.QA, actionItem.status)
    }

    @Test
    fun `담당자는 QA 액션아이템을 RELEASED로 전환할 수 있다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.QA)

        actionItem.completeQA(requestUserId = 2L)

        assertEquals(ActionItemStatus.RELEASED, actionItem.status)
    }

    // ==============================
    // 상태 전이 — 불가 (잘못된 순서)
    // ==============================

    @Test
    fun `CANDIDATE 상태의 액션아이템은 IN_PROGRESS로 직접 전환할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.CANDIDATE)

        assertThrows<InvalidStateTransitionException> {
            actionItem.startProgress(requestUserId = 2L)
        }
    }

    @Test
    fun `PICKED_UP 상태의 액션아이템은 바로 배포 대기로 전환할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.PICKED_UP)

        assertThrows<InvalidStateTransitionException> {
            actionItem.submitForDeploy(requestUserId = 2L)
        }
    }

    @Test
    fun `RELEASED 상태의 액션아이템은 상태를 변경할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.RELEASED)

        assertThrows<InvalidStateTransitionException> {
            actionItem.startProgress(requestUserId = 2L)
        }
    }

    // ==============================
    // 상태 전이 — 불가 (담당자 아님)
    // ==============================

    @Test
    fun `담당자가 아닌 유저는 IN_PROGRESS로 전환할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.PICKED_UP)

        assertThrows<UnauthorizedActionException> {
            actionItem.startProgress(requestUserId = 3L)
        }
    }

    @Test
    fun `담당자가 아닌 유저는 배포 대기로 전환할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.IN_PROGRESS)

        assertThrows<UnauthorizedActionException> {
            actionItem.submitForDeploy(requestUserId = 3L)
        }
    }

    @Test
    fun `담당자가 아닌 유저는 QA로 전환할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.DEPLOY_WAITING)

        assertThrows<UnauthorizedActionException> {
            actionItem.completeDeploy(requestUserId = 3L)
        }
    }

    @Test
    fun `담당자가 아닌 유저는 RELEASED로 전환할 수 없다`() {
        val actionItem = 액션아이템_생성(assigneeUserId = 2L, status = ActionItemStatus.QA)

        assertThrows<UnauthorizedActionException> {
            actionItem.completeQA(requestUserId = 3L)
        }
    }

    // ==============================
    // changePriority
    // ==============================

    @Test
    fun `입안자는 액션아이템의 우선순위를 변경할 수 있다`() {
        val actionItem = 액션아이템_생성(proposerUserId = 1L, priority = ActionItemPriority.MEDIUM)

        actionItem.changePriority(requestUserId = 1L, priority = ActionItemPriority.HIGH)

        assertEquals(ActionItemPriority.HIGH, actionItem.priority)
    }

    @Test
    fun `입안자가 아닌 유저는 우선순위를 변경할 수 없다`() {
        val actionItem = 액션아이템_생성(proposerUserId = 1L)

        assertThrows<UnauthorizedActionException> {
            actionItem.changePriority(requestUserId = 2L, priority = ActionItemPriority.HIGH)
        }
    }

    @Test
    fun `RELEASED 상태의 액션아이템은 우선순위를 변경할 수 없다`() {
        val actionItem = 액션아이템_생성(proposerUserId = 1L, status = ActionItemStatus.RELEASED)

        assertThrows<InvalidStateTransitionException> {
            actionItem.changePriority(requestUserId = 1L, priority = ActionItemPriority.LOW)
        }
    }

    // ==============================
    // confirm
    // ==============================

    @Test
    fun `액션아이템을 확인 처리할 수 있다`() {
        val actionItem = 액션아이템_생성()

        actionItem.confirm()

        assertTrue(actionItem.isConfirmed)
    }

    @Test
    fun `이미 확인된 액션아이템을 다시 확인하면 예외가 발생한다`() {
        val actionItem = 액션아이템_생성()
        actionItem.confirm()

        assertThrows<InvalidStateTransitionException> {
            actionItem.confirm()
        }
    }
}