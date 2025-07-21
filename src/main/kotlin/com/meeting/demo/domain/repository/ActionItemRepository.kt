package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.ActionItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ActionItemRepository : JpaRepository<ActionItem, Long> {
    
    fun findByMeetingId(meetingId: Long): List<ActionItem>
    
    fun findByTicketId(ticketId: Long): List<ActionItem>
    
    fun findByAssigneeUserId(assigneeUserId: Long): List<ActionItem>
    
    fun findByIsConfirmedTrue(): List<ActionItem>
    
    fun findByIsConfirmedFalse(): List<ActionItem>
    
    fun findByMeetingIdAndIsConfirmedFalse(meetingId: Long): List<ActionItem>
    
    @Query("SELECT ai FROM ActionItem ai WHERE ai.assigneeUserId = :userId AND ai.isConfirmed = false")
    fun findPendingActionItemsByUser(@Param("userId") userId: Long): List<ActionItem>
    
    @Query("SELECT ai FROM ActionItem ai WHERE ai.ticketId IS NOT NULL AND ai.ticketId = :ticketId")
    fun findLinkedActionItemsByTicket(@Param("ticketId") ticketId: Long): List<ActionItem>
}