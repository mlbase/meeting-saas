package com.meeting.demo.domain.vo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EmailTest {
    
    // Valid email cases
    @Test
    fun `should accept standard email format`() {
        val email = Email("user@example.com")
        assertEquals("user@example.com", email.value)
    }
    
    @Test
    fun `should accept email with subdomain`() {
        val email = Email("user@mail.example.com")
        assertEquals("user@mail.example.com", email.value)
    }
    
    @Test
    fun `should accept email with numbers in username`() {
        val email = Email("user123@example.com")
        assertEquals("user123@example.com", email.value)
    }
    
    @Test
    fun `should accept email with dots in username`() {
        val email = Email("first.last@example.com")
        assertEquals("first.last@example.com", email.value)
    }
    
    @Test
    fun `should accept email with plus sign in username`() {
        val email = Email("user+tag@example.com")
        assertEquals("user+tag@example.com", email.value)
    }
    
    @Test
    fun `should accept email with underscore in username`() {
        val email = Email("user_name@example.com")
        assertEquals("user_name@example.com", email.value)
    }
    
    @Test
    fun `should accept email with hyphen in username`() {
        val email = Email("user-name@example.com")
        assertEquals("user-name@example.com", email.value)
    }
    
    @Test
    fun `should accept email with long TLD`() {
        val email = Email("user@example.museum")
        assertEquals("user@example.museum", email.value)
    }
    
    @Test
    fun `should accept email with numbers in domain`() {
        val email = Email("user@example123.com")
        assertEquals("user@example123.com", email.value)
    }
    
    @Test
    fun `should accept email with hyphens in domain`() {
        val email = Email("user@my-site.com")
        assertEquals("user@my-site.com", email.value)
    }
    
    // Invalid email cases - missing @ symbol
    @Test
    fun `should reject email without at symbol`() {
        assertThrows<IllegalArgumentException> {
            Email("userexample.com")
        }
    }
    
    // Invalid email cases - missing domain
    @Test
    fun `should reject email without domain`() {
        assertThrows<IllegalArgumentException> {
            Email("user@")
        }
    }
    
    @Test
    fun `should reject email with only username`() {
        assertThrows<IllegalArgumentException> {
            Email("user")
        }
    }
    
    // Invalid email cases - missing username
    @Test
    fun `should reject email without username`() {
        assertThrows<IllegalArgumentException> {
            Email("@example.com")
        }
    }
    
    // Invalid email cases - domain without TLD
    @Test
    fun `should reject email without domain extension`() {
        assertThrows<IllegalArgumentException> {
            Email("user@example")
        }
    }
    
    // Invalid email cases - multiple @ symbols
    @Test
    fun `should reject email with multiple at symbols`() {
        assertThrows<IllegalArgumentException> {
            Email("user@@example.com")
        }
    }
    
    @Test
    fun `should reject email with at symbols in wrong places`() {
        assertThrows<IllegalArgumentException> {
            Email("user@example@.com")
        }
    }
    
    // Invalid email cases - spaces
    @Test
    fun `should reject email with spaces in username`() {
        assertThrows<IllegalArgumentException> {
            Email("user name@example.com")
        }
    }
    
    @Test
    fun `should reject email with spaces in domain`() {
        assertThrows<IllegalArgumentException> {
            Email("user@example .com")
        }
    }
    
    // Invalid email cases - empty and blank
    @Test
    fun `should reject empty email`() {
        assertThrows<IllegalArgumentException> {
            Email("")
        }
    }
    
    @Test
    fun `should reject blank email`() {
        assertThrows<IllegalArgumentException> {
            Email("   ")
        }
    }
    
    // Edge cases - very short and long emails
    @Test
    fun `should accept minimal valid email`() {
        val email = Email("a@b.co")
        assertEquals("a@b.co", email.value)
    }
    
    @Test
    fun `should accept long but valid email`() {
        val longEmail = "very.long.username.with.many.dots@very.long.domain.name.with.many.subdomains.com"
        val email = Email(longEmail)
        assertEquals(longEmail, email.value)
    }
    
    // Test static validation method
    @Test
    fun `isValid should return true for valid emails`() {
        assertTrue(Email.isValid("user@example.com"))
        assertTrue(Email.isValid("test.email+tag@domain.co.uk"))
        assertTrue(Email.isValid("user123@test-site.org"))
    }
    
    @Test
    fun `isValid should return false for invalid emails`() {
        assertFalse(Email.isValid("invalid-email"))
        assertFalse(Email.isValid("user@"))
        assertFalse(Email.isValid("@domain.com"))
        assertFalse(Email.isValid("user@domain"))
        assertFalse(Email.isValid(""))
        assertFalse(Email.isValid("user name@domain.com"))
    }
    
    // Test safe creation method
    @Test
    fun `createOrNull should return Email for valid email`() {
        val email = Email.createOrNull("valid@example.com")
        assertNotNull(email)
        assertEquals("valid@example.com", email!!.value)
    }
    
    @Test
    fun `createOrNull should return null for invalid email`() {
        assertNull(Email.createOrNull("invalid-email"))
        assertNull(Email.createOrNull(""))
        assertNull(Email.createOrNull(null))
        assertNull(Email.createOrNull("   "))
    }
    
    // Test value object equality
    @Test
    fun `should support equality comparison`() {
        val email1 = Email("test@example.com")
        val email2 = Email("test@example.com")
        val email3 = Email("different@example.com")
        
        assertEquals(email1, email2)
        assertTrue(email1 == email2)
        assertFalse(email1 == email3)
    }
    
    @Test
    fun `should have consistent hashCode`() {
        val email1 = Email("test@example.com")
        val email2 = Email("test@example.com")
        
        assertEquals(email1.hashCode(), email2.hashCode())
    }
    
    @Test
    fun `toString should return email value`() {
        val email = Email("test@example.com")
        assertEquals("test@example.com", email.toString())
    }
    
    // Test case sensitivity
    @Test
    fun `should treat emails as case sensitive in value object`() {
        val email1 = Email("User@Example.Com")
        val email2 = Email("user@example.com")
        
        // Value objects preserve exact case
        assertEquals("User@Example.Com", email1.value)
        assertEquals("user@example.com", email2.value)
        assertFalse(email1 == email2)
    }
    
    // International domain names (basic test)
    @Test
    fun `should handle basic international characters in domain`() {
        // Note: This depends on your regex implementation
        // The current simple regex might not support full internationalization
        assertThrows<IllegalArgumentException> {
            Email("user@münchen.de")
        }
    }
    
    // Business logic edge cases
    @Test
    fun `should reject obviously malicious input`() {
        assertThrows<IllegalArgumentException> {
            Email("'; DROP TABLE users; --")
        }
        
        assertThrows<IllegalArgumentException> {
            Email("<script>alert('xss')</script>@example.com")
        }
    }
}