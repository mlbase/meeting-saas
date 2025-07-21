package com.meeting.demo.model

data class Participant(
    val id: Long? = null,
    val name: String,
    val role: String,
    val department: String,
    val email: String? = null,
    val password: String? = null
)

data class UserData(
    val id: Long? = null,
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true,
    val roles: List<String> = listOf(),
    val companyId: Long? = null
)

data class VoiceProfile(
    val name: String,
    val voiceCharacteristics: String // e.g., "Deep voice, slow speech, British accent"
)

data class SpeakerSegment(
    val speaker: String,
    val startTime: String,
    val endTime: String,
    val text: String,
    val confidence: Double? = null
)

data class MeetingAnalysis(
    val speakers: List<SpeakerInfo>,
    val roleAnalysis: String,
    val speakingTimeDistribution: Map<String, Int>,
    val keyInsights: List<String>
)

data class SpeakerInfo(
    val name: String,
    val estimatedRole: String,
    val speakingTime: Int, // in seconds
    val keyContributions: List<String>,
    val communicationStyle: String
)