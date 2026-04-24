package com.meeting.demo.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MeetingTest {

    private fun 미팅_생성(
        hostUserId: Long = 1L,
        companyId: Long = 1L,
        status: MeetingStatus = MeetingStatus.SCHEDULED
    ) = Meeting(
        title = "주간 회의",
        hostUserId = hostUserId,
        companyId = companyId,
        startTime = LocalDateTime.now().plusHours(1),
        status = status
    )

    // ==============================
    // 기본 상태
    // ==============================

    @Test
    fun `미팅의 기본 상태는 SCHEDULED이다`() {
        val meeting = 미팅_생성()
        assertEquals(MeetingStatus.SCHEDULED, meeting.status)
    }

    // ==============================
    // dropMeeting
    // ==============================

    @Test
    fun `SCHEDULED 상태의 미팅을 드롭할 수 있다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SCHEDULED)

        meeting.drop()

        assertEquals(MeetingStatus.DROPPED, meeting.status)
    }

    @Test
    fun `IN_PROGRESS 상태의 미팅은 드롭할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.IN_PROGRESS)

        assertThrows<IllegalStateException> {
            meeting.drop()
        }
    }

    @Test
    fun `SUBMITTED 상태의 미팅은 드롭할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SUBMITTED)

        assertThrows<IllegalStateException> {
            meeting.drop()
        }
    }

    @Test
    fun `COMPLETED 상태의 미팅은 드롭할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.COMPLETED)

        assertThrows<IllegalStateException> {
            meeting.drop()
        }
    }

    // ==============================
    // start (SCHEDULED → IN_PROGRESS)
    // ==============================

    @Test
    fun `SCHEDULED 상태의 미팅을 시작할 수 있다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SCHEDULED)

        meeting.start()

        assertEquals(MeetingStatus.IN_PROGRESS, meeting.status)
    }

    @Test
    fun `DROPPED 상태의 미팅은 시작할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.DROPPED)

        assertThrows<IllegalStateException> {
            meeting.start()
        }
    }

    // ==============================
    // submit (IN_PROGRESS → SUBMITTED)
    // ==============================

    @Test
    fun `IN_PROGRESS 상태의 미팅을 제출할 수 있다`() {
        val meeting = 미팅_생성(status = MeetingStatus.IN_PROGRESS)

        meeting.submit()

        assertEquals(MeetingStatus.SUBMITTED, meeting.status)
    }

    @Test
    fun `SCHEDULED 상태의 미팅은 제출할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SCHEDULED)

        assertThrows<IllegalStateException> {
            meeting.submit()
        }
    }

    // ==============================
    // startBatchProcessing (SUBMITTED → BATCH_PROCESSING)
    // ==============================

    @Test
    fun `SUBMITTED 상태의 미팅은 배치 처리를 시작할 수 있다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SUBMITTED)

        meeting.startBatchProcessing()

        assertEquals(MeetingStatus.BATCH_PROCESSING, meeting.status)
    }

    @Test
    fun `IN_PROGRESS 상태의 미팅은 배치 처리를 시작할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.IN_PROGRESS)

        assertThrows<IllegalStateException> {
            meeting.startBatchProcessing()
        }
    }

    // ==============================
    // complete (BATCH_PROCESSING → COMPLETED)
    // ==============================

    @Test
    fun `BATCH_PROCESSING 상태의 미팅을 완료할 수 있다`() {
        val meeting = 미팅_생성(status = MeetingStatus.BATCH_PROCESSING)

        meeting.complete()

        assertEquals(MeetingStatus.COMPLETED, meeting.status)
    }

    @Test
    fun `SUBMITTED 상태의 미팅은 완료할 수 없다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SUBMITTED)

        assertThrows<IllegalStateException> {
            meeting.complete()
        }
    }

    // ==============================
    // addParticipant
    // ==============================

    @Test
    fun `미팅에 참가자를 추가할 수 있다`() {
        val meeting = 미팅_생성()

        meeting.addParticipant(userId = 2L)

        assertTrue(meeting.hasParticipant(userId = 2L))
    }

    @Test
    fun `이미 추가된 참가자를 다시 추가할 수 없다`() {
        val meeting = 미팅_생성()
        meeting.addParticipant(userId = 2L)

        assertThrows<IllegalArgumentException> {
            meeting.addParticipant(userId = 2L)
        }
    }

    // ==============================
    // removeParticipant
    // ==============================

    @Test
    fun `미팅에서 참가자를 제거할 수 있다`() {
        val meeting = 미팅_생성()
        meeting.addParticipant(userId = 2L)

        meeting.removeParticipant(userId = 2L)

        assertFalse(meeting.hasParticipant(userId = 2L))
    }

    @Test
    fun `존재하지 않는 참가자를 제거하려 하면 예외가 발생한다`() {
        val meeting = 미팅_생성()

        assertThrows<IllegalArgumentException> {
            meeting.removeParticipant(userId = 999L)
        }
    }

    // ==============================
    // COMPLETED 상태 체크
    // ==============================

    @Test
    fun `COMPLETED 상태의 미팅은 isCompleted가 true이다`() {
        val meeting = 미팅_생성(status = MeetingStatus.COMPLETED)
        assertTrue(meeting.isCompleted())
    }

    @Test
    fun `COMPLETED가 아닌 미팅은 isCompleted가 false이다`() {
        val meeting = 미팅_생성(status = MeetingStatus.SCHEDULED)
        assertFalse(meeting.isCompleted())
    }
}
