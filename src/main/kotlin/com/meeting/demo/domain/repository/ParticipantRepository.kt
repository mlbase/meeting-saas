package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {
    
    fun findByMeetingId(meetingId: Long): List<Participant>
    
    fun findByUserId(userId: Long): List<Participant>
    
    fun findByUserIdAndMeetingId(userId: Long, meetingId: Long): Participant?
    
    fun existsByUserIdAndMeetingId(userId: Long, meetingId: Long): Boolean
    
    @Query("SELECT p FROM Participant p WHERE p.meetingId = :meetingId AND p.leftAt IS NULL")
    fun findActiveParticipantsByMeeting(@Param("meetingId") meetingId: Long): List<Participant>
    
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.meetingId = :meetingId")
    fun countParticipantsByMeeting(@Param("meetingId") meetingId: Long): Long
}