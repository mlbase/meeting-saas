package com.meeting.demo.domain.model

data class Participant(
    val name: String,
    val role: String,
    val department: String,
    val email: String? = null
)