package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.Ticket
import com.meeting.demo.domain.model.TicketStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {
    
    fun findByUserId(userId: Long): List<Ticket>
    
    fun findByCompanyId(companyId: Long): List<Ticket>
    
    fun findByStatus(status: TicketStatus): List<Ticket>
    
    fun findByUserIdAndStatus(userId: Long, status: TicketStatus): List<Ticket>
    
    fun findByCompanyIdAndStatus(companyId: Long, status: TicketStatus): List<Ticket>
    
    fun findByIsClosedFalse(): List<Ticket>
    
    @Query("SELECT t FROM Ticket t WHERE t.companyId = :companyId AND t.isClosed = false")
    fun findOpenTicketsByCompany(@Param("companyId") companyId: Long): List<Ticket>
    
    fun findByGithubRepositoryId(githubRepositoryId: String): List<Ticket>
}