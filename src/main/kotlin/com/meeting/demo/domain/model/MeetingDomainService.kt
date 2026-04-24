package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.DuplicateActiveMeetingException
import com.meeting.demo.domain.exception.UnauthorizedActionException
import com.meeting.demo.domain.repository.MeetingRepository
import java.time.LocalDateTime

class MeetingDomainService(
    private val meetingRepository: MeetingRepository
) {
    fun createMeeting(host: User, title: String, companyId: Long, startTime: LocalDateTime): Meeting {
        if (!host.isCLevel())
            throw UnauthorizedActionException("C레벨 이상만 미팅을 생성할 수 있습니다.")
        if (meetingRepository.existsActiveByCompanyId(companyId))
            throw DuplicateActiveMeetingException("이미 진행 중인 미팅이 있습니다.")
        return Meeting(title = title, hostUserId = host.id!!, companyId = companyId, startTime = startTime)
    }
}