package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.DuplicateActiveMeetingException
import com.meeting.demo.domain.exception.UnauthorizedActionException
import com.meeting.demo.domain.repository.MeetingRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.test.assertNotNull

class MeetingDomainServiceTest {

    private val meetingRepository: MeetingRepository = mock()
    private val meetingDomainService = MeetingDomainService(meetingRepository)

    private fun c레벨_유저() = User.create(
        username = "clevel",
        email = "clevel@company.com",
        firstName = "C",
        lastName = "Level",
        password = "password123",
        roles = "C_LEVEL",
        companyId = 1L
    ).also { it.id = 1L }

    private fun 일반_유저() = User.create(
        username = "member",
        email = "member@company.com",
        firstName = "Member",
        lastName = "User",
        password = "password123",
        roles = "MEMBER",
        companyId = 1L
    ).also { it.id = 2L }

    // ==============================
    // createMeeting — role 검증
    // ==============================

    @Test
    fun `C레벨 유저는 미팅을 생성할 수 있다`() {
        val host = c레벨_유저()
        whenever(meetingRepository.existsActiveByCompanyId(companyId = 1L)).thenReturn(false)

        val meeting = meetingDomainService.createMeeting(
            host = host,
            title = "전략 회의",
            companyId = 1L,
            startTime = LocalDateTime.now().plusHours(1)
        )

        assertNotNull(meeting)
    }

    @Test
    fun `C레벨 미만 유저는 미팅을 생성할 수 없다`() {
        val host = 일반_유저()

        assertThrows<UnauthorizedActionException> {
            meetingDomainService.createMeeting(
                host = host,
                title = "전략 회의",
                companyId = 1L,
                startTime = LocalDateTime.now().plusHours(1)
            )
        }
    }

    // ==============================
    // createMeeting — 중복 생성 차단
    // ==============================

    @Test
    fun `회사 내 진행 중인 미팅이 없으면 새 미팅을 생성할 수 있다`() {
        val host = c레벨_유저()
        whenever(meetingRepository.existsActiveByCompanyId(companyId = 1L)).thenReturn(false)

        val meeting = meetingDomainService.createMeeting(
            host = host,
            title = "주간 회의",
            companyId = 1L,
            startTime = LocalDateTime.now().plusHours(1)
        )

        assertNotNull(meeting)
    }

    @Test
    fun `회사 내 이미 진행 중인 미팅이 있으면 새 미팅을 생성할 수 없다`() {
        val host = c레벨_유저()
        whenever(meetingRepository.existsActiveByCompanyId(companyId = 1L)).thenReturn(true)

        assertThrows<DuplicateActiveMeetingException> {
            meetingDomainService.createMeeting(
                host = host,
                title = "주간 회의",
                companyId = 1L,
                startTime = LocalDateTime.now().plusHours(1)
            )
        }
    }
}
