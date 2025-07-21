package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.Meeting
import com.meeting.demo.domain.model.MeetingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MeetingRepository : JpaRepository<Meeting, Long> {
    
    fun findByHostUserId(hostUserId: Long): List<Meeting>
    
    fun findByCompanyId(companyId: Long): List<Meeting>
    
    fun findByStatus(status: MeetingStatus): List<Meeting>
    
    fun findByCompanyIdAndStatus(companyId: Long, status: MeetingStatus): List<Meeting>
    
    @Query("SELECT m FROM Meeting m WHERE m.startTime BETWEEN :startDate AND :endDate")
    fun findByStartTimeBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Meeting>
    
    @Query("SELECT m FROM Meeting m WHERE m.companyId = :companyId AND m.startTime >= :fromDate ORDER BY m.startTime")
    fun findUpcomingMeetingsByCompany(
        @Param("companyId") companyId: Long,
        @Param("fromDate") fromDate: LocalDateTime = LocalDateTime.now()
    ): List<Meeting>
    
    @Query("SELECT m FROM Meeting m JOIN Participant p ON m.id = p.meetingId WHERE p.userId = :userId")
    fun findMeetingsByParticipant(@Param("userId") userId: Long): List<Meeting>
}