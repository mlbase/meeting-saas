package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    
    fun findByNameContainingIgnoreCase(name: String): List<Company>
    
    fun findByIsActiveTrue(): List<Company>
    
    @Query("SELECT c FROM Company c WHERE c.isActive = true AND c.name LIKE %:name%")
    fun findActiveCompaniesByName(@Param("name") name: String): List<Company>
}