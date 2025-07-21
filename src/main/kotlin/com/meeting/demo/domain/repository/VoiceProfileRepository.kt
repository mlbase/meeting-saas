package com.meeting.demo.domain.repository

import com.meeting.demo.domain.model.VoiceProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoiceProfileRepository : JpaRepository<VoiceProfile, Long> {
    
    fun findByUserId(userId: Long): VoiceProfile?
    
    fun existsByUserId(userId: Long): Boolean
    
    fun deleteByUserId(userId: Long): Long
}