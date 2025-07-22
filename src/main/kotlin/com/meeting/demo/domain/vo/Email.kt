package com.meeting.demo.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Email(
    @Column(name = "email", unique = true)
    val value: String
) {
    init {
        require(isValid(value)) { "Invalid email: $value" }
    }
    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"

        fun isValid(email: String): Boolean {
            return email.matches(Regex(EMAIL_REGEX))
        }

        fun createOrNull(email: String?): Email? {
            return if (email.isNullOrBlank() || !isValid(email)) {
                null
            } else {
                Email(email)
            }
        }
    }

    override fun toString(): String = value
}