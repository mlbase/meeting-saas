package com.meeting.demo.domain.vo

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Embeddable
data class Password private constructor(
    @Column(name = "password")
    val hashedValue: String
) {
    companion object {
        private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        
        /**
         * Create a Password from plain text - will be automatically hashed
         */
        fun create(plainTextPassword: String): Password {
            require(plainTextPassword.isNotBlank()) { "Password cannot be blank" }
            require(plainTextPassword.length >= 6) { "Password must be at least 6 characters long" }
            require(plainTextPassword.length <= 128) { "Password cannot exceed 128 characters" }
            
            val hashedPassword = passwordEncoder.encode(plainTextPassword)
            return Password(hashedPassword)
        }
        
        /**
         * Create a Password from already hashed value (for database loading)
         */
        fun fromHash(hashedPassword: String): Password {
            require(hashedPassword.isNotBlank()) { "Hashed password cannot be blank" }
            return Password(hashedPassword)
        }
        
        /**
         * Create a strong password with validation
         */
        fun createStrong(plainTextPassword: String): Password {
            require(isStrongPassword(plainTextPassword)) { 
                "Password must be at least 8 characters with uppercase, lowercase, number, and special character" 
            }
            return create(plainTextPassword)
        }
        
        /**
         * Validate password strength
         */
        fun isStrongPassword(plainText: String): Boolean {
            if (plainText.length < 8) return false
            
            var hasUpper = false
            var hasLower = false
            var hasDigit = false
            var hasSpecial = false
            
            for (char in plainText) {
                when {
                    char.isUpperCase() -> hasUpper = true
                    char.isLowerCase() -> hasLower = true
                    char.isDigit() -> hasDigit = true
                    char in "!@#$%^&*()-_=+[]{}|;:',.<>?/" -> hasSpecial = true
                }
            }
            
            return hasUpper && hasLower && hasDigit && hasSpecial
        }
        
        /**
         * Basic password validation
         */
        fun isValid(plainText: String): Boolean {
            return plainText.isNotBlank() && 
                   plainText.length >= 6 && 
                   plainText.length <= 128
        }
    }
    
    /**
     * Verify if a plain text password matches this hashed password
     */
    fun matches(plainTextPassword: String): Boolean {
        return passwordEncoder.matches(plainTextPassword, hashedValue)
    }
    
    /**
     * Check if password needs rehashing (for security updates)
     */
    fun needsRehashing(): Boolean {
        return passwordEncoder.upgradeEncoding(hashedValue)
    }
    
    override fun toString(): String = "Password(***)"
}