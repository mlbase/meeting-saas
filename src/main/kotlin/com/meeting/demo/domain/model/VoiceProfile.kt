package com.meeting.demo.domain.model

import jakarta.persistence.*

@Entity
@Table(name = "voice_profiles")
class VoiceProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: Long,
    
    @Column(name = "voice_characteristics", columnDefinition = "TEXT")
    val voiceCharacteristics: String
) : BaseEntity()