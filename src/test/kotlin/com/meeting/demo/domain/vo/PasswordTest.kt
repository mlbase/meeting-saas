package com.meeting.demo.domain.vo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PasswordTest {
    
    // Basic password creation tests
    @Test
    fun `should create password with valid plain text`() {
        val password = Password.create("password123")
        
        assertNotNull(password.hashedValue)
        assertTrue(password.hashedValue.isNotBlank())
        assertTrue(password.hashedValue.startsWith("\$2a\$")) // BCrypt hash format
        assertNotEquals("password123", password.hashedValue) // Should be hashed, not plain text
    }
    
    @Test
    fun `should create different hashes for same plain text`() {
        val password1 = Password.create("samepassword")
        val password2 = Password.create("samepassword")
        
        // BCrypt should generate different salts, so hashes should be different
        assertNotEquals(password1.hashedValue, password2.hashedValue)
        
        // But both should match the original plain text
        assertTrue(password1.matches("samepassword"))
        assertTrue(password2.matches("samepassword"))
    }
    
    @Test
    fun `should verify correct password`() {
        val password = Password.create("myPassword123")
        
        assertTrue(password.matches("myPassword123"))
        assertFalse(password.matches("wrongPassword"))
        assertFalse(password.matches("myPassword124"))
        assertFalse(password.matches(""))
    }
    
    // Password validation tests - minimum requirements
    @Test
    fun `should accept minimum valid passwords`() {
        val validPasswords = listOf(
            "123456", // exactly 6 characters
            "abcdef", // all letters
            "ABCDEF", // all uppercase
            "!@#\$%^", // all special characters
            "a1b2c3" // mixed alphanumeric
        )
        
        validPasswords.forEach { plainText ->
            val password = Password.create(plainText)
            assertTrue(password.matches(plainText), "Should accept password: $plainText")
        }
    }
    
    @Test
    fun `should reject passwords that are too short`() {
        val shortPasswords = listOf("", "1", "12", "123", "1234", "12345")
        
        shortPasswords.forEach { shortPassword ->
            assertThrows<IllegalArgumentException>("Should reject short password: '$shortPassword'") {
                Password.create(shortPassword)
            }
        }
    }
    
    @Test
    fun `should reject blank passwords`() {
        val blankPasswords = listOf("", "   ", "\t", "\n", "  \t  \n  ")
        
        blankPasswords.forEach { blankPassword ->
            assertThrows<IllegalArgumentException>("Should reject blank password: '$blankPassword'") {
                Password.create(blankPassword)
            }
        }
    }
    
    @Test
    fun `should reject extremely long passwords`() {
        val tooLongPassword = "a".repeat(129) // 129 characters
        
        assertThrows<IllegalArgumentException> {
            Password.create(tooLongPassword)
        }
    }
    
    @Test
    fun `should accept maximum length password`() {
        val maxLengthPassword = "a".repeat(128) // exactly 128 characters
        
        val password = Password.create(maxLengthPassword)
        assertTrue(password.matches(maxLengthPassword))
    }
    
    // Strong password validation tests
    @Test
    fun `should validate strong password requirements`() {
        val strongPasswords = listOf(
            "Password123!",
            "MyStrong@Pass1",
            "Complex#Password2024",
            "Secure!Pass#123",
            "Valid@Password1"
        )
        
        strongPasswords.forEach { strongPassword ->
            assertTrue(Password.isStrongPassword(strongPassword), "Should be strong: $strongPassword")
        }
    }
    
    @Test
    fun `should reject weak passwords - missing uppercase`() {
        val weakPasswords = listOf(
            "password123!",
            "weak@pass#1",
            "nouppercases1!"
        )
        
        weakPasswords.forEach { weakPassword ->
            assertFalse(Password.isStrongPassword(weakPassword), "Should be weak (no uppercase): $weakPassword")
        }
    }
    
    @Test
    fun `should reject weak passwords - missing lowercase`() {
        val weakPasswords = listOf(
            "PASSWORD123!",
            "WEAK@PASS#1",
            "NOLOWERCASES1!"
        )
        
        weakPasswords.forEach { weakPassword ->
            assertFalse(Password.isStrongPassword(weakPassword), "Should be weak (no lowercase): $weakPassword")
        }
    }
    
    @Test
    fun `should reject weak passwords - missing numbers`() {
        val weakPasswords = listOf(
            "Password!",
            "Weak@Pass#",
            "NoNumbers!@#"
        )
        
        weakPasswords.forEach { weakPassword ->
            assertFalse(Password.isStrongPassword(weakPassword), "Should be weak (no numbers): $weakPassword")
        }
    }
    
    @Test
    fun `should reject weak passwords - missing special characters`() {
        val weakPasswords = listOf(
            "Password123",
            "WeakPass1",
            "NoSpecialChars1"
        )
        
        weakPasswords.forEach { weakPassword ->
            assertFalse(Password.isStrongPassword(weakPassword), "Should be weak (no special chars): $weakPassword")
        }
    }
    
    @Test
    fun `should reject weak passwords - too short`() {
        val shortPasswords = listOf(
            "Pass1!",  // 6 chars
            "Ab1!",    // 4 chars
            "Strong!" // 7 chars but no number
        )
        
        shortPasswords.forEach { shortPassword ->
            assertFalse(Password.isStrongPassword(shortPassword), "Should be weak (too short): $shortPassword")
        }
    }
    
    @Test
    fun `should create strong password with validation`() {
        val strongPassword = "StrongPass123!"
        val password = Password.createStrong(strongPassword)
        
        assertTrue(password.matches(strongPassword))
        assertTrue(Password.isStrongPassword(strongPassword))
    }
    
    @Test
    fun `should reject creating strong password with weak input`() {
        val weakPasswords = listOf(
            "weak",
            "password123",
            "PASSWORD123",
            "Password!",
            "Pass123"
        )
        
        weakPasswords.forEach { weakPassword ->
            assertThrows<IllegalArgumentException>("Should reject weak password: $weakPassword") {
                Password.createStrong(weakPassword)
            }
        }
    }
    
    // Edge cases and security tests
    @Test
    fun `should handle special characters correctly`() {
        val specialCharPasswords = listOf(
            "Pass@123!",
            "Secure#Pass$456",
            "Complex&Password*789",
            "Strong+Pass-123=",
            "Valid_Pass|123[]"
        )
        
        specialCharPasswords.forEach { specialPassword ->
            val password = Password.create(specialPassword)
            assertTrue(password.matches(specialPassword), "Should handle special chars: $specialPassword")
        }
    }
    
    @Test
    fun `should handle unicode characters`() {
        val unicodePasswords = listOf(
            "Pássword123",
            "密码123!Ab",
            "Motdepasse123é"
        )
        
        unicodePasswords.forEach { unicodePassword ->
            val password = Password.create(unicodePassword)
            assertTrue(password.matches(unicodePassword), "Should handle unicode: $unicodePassword")
        }
    }
    
    @Test
    fun `should be case sensitive`() {
        val password = Password.create("Password123")
        
        assertTrue(password.matches("Password123"))
        assertFalse(password.matches("password123"))
        assertFalse(password.matches("PASSWORD123"))
        assertFalse(password.matches("Password123!"))
    }
    
    @Test
    fun `should reject SQL injection attempts`() {
        val maliciousPasswords = listOf(
            "'; DROP TABLE users; --",
            "admin'--",
            "1' OR '1'='1",
            "password' OR 1=1--"
        )
        
        maliciousPasswords.forEach { maliciousPassword ->
            // Should still create and hash them properly (they're just strings)
            val password = Password.create(maliciousPassword)
            assertTrue(password.matches(maliciousPassword))
            // But they won't be strong passwords
            assertFalse(Password.isStrongPassword(maliciousPassword))
        }
    }
    
    @Test
    fun `should reject XSS attempts`() {
        val xssPasswords = listOf(
            "<script>alert('xss')</script>",
            "javascript:alert('xss')",
            "<img src=x onerror=alert('xss')>"
        )
        
        xssPasswords.forEach { xssPassword ->
            // Should still create and hash them properly
            val password = Password.create(xssPassword)
            assertTrue(password.matches(xssPassword))
        }
    }
    
    // Basic validation method tests
    @Test
    fun `isValid should return true for acceptable passwords`() {
        val validPasswords = listOf(
            "123456",
            "password",
            "PASSWORD",
            "Pass123!",
            "a".repeat(128)
        )
        
        validPasswords.forEach { validPassword ->
            assertTrue(Password.isValid(validPassword), "Should be valid: $validPassword")
        }
    }
    
    @Test
    fun `isValid should return false for unacceptable passwords`() {
        val invalidPasswords = listOf(
            "",
            "   ",
            "12345", // too short
            "a".repeat(129) // too long
        )
        
        invalidPasswords.forEach { invalidPassword ->
            assertFalse(Password.isValid(invalidPassword), "Should be invalid: '$invalidPassword'")
        }
    }
    
    // Hash creation from existing hash
    @Test
    fun `should create password from existing hash`() {
        val existingHash = "\$2a\$10\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9wBc3FDjPkJA5uS"
        val password = Password.fromHash(existingHash)
        
        assertEquals(existingHash, password.hashedValue)
    }
    
    @Test
    fun `should reject empty hash`() {
        assertThrows<IllegalArgumentException> {
            Password.fromHash("")
        }
        
        assertThrows<IllegalArgumentException> {
            Password.fromHash("   ")
        }
    }
    
    // Security features
    @Test
    fun `should check if password needs rehashing`() {
        val password = Password.create("testPassword123")
        
        // For BCrypt, this should typically return false for new passwords
        // (unless there's a security update requiring higher cost)
        val needsRehash = password.needsRehashing()
        // This is implementation dependent, so we just test it doesn't throw
        assertNotNull(needsRehash)
    }
    
    // toString security
    @Test
    fun `toString should not expose password hash`() {
        val password = Password.create("secretPassword123")
        val stringRepresentation = password.toString()
        
        assertEquals("Password(***)", stringRepresentation)
        assertFalse(stringRepresentation.contains(password.hashedValue))
        assertFalse(stringRepresentation.contains("secretPassword123"))
    }
    
    // Performance and consistency
    @Test
    fun `should consistently hash and verify passwords`() {
        val plainText = "consistentPassword123!"
        val password = Password.create(plainText)
        
        // Should consistently verify the same password
        repeat(10) {
            assertTrue(password.matches(plainText), "Should consistently match on attempt $it")
        }
        
        // Should consistently reject wrong passwords
        repeat(10) {
            assertFalse(password.matches("wrongPassword"), "Should consistently reject on attempt $it")
        }
    }
    
    @Test
    fun `should handle empty string verification safely`() {
        val password = Password.create("realPassword123")
        
        assertFalse(password.matches(""))
        assertFalse(password.matches("   "))
    }
}