package com.meeting.demo.domain.model

import com.meeting.demo.domain.vo.Email
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserTest {

    @Test
    fun `should create user with valid data`() {
        val user = User.create(
            username = "johndoe",
            email = "john.doe@example.com",
            firstName = "John",
            lastName = "Doe",
            password = "password123"
        )

        assertEquals("johndoe", user.username)
        assertEquals("john.doe@example.com", user.email.value)
        assertEquals("John", user.firstName)
        assertEquals("Doe", user.lastName)
        assertTrue(user.isActive)
    }

    @Test
    fun `should create user with company role`() {
        val user = User.create(
            username = "manager",
            email = "manager@company.com",
            firstName = "Manager",
            lastName = "User",
            password = "password123",
            roles = "ROLE_COMPANY_ADMIN",
            companyId = 1L
        )

        assertEquals("ROLE_COMPANY_ADMIN", user.roles)
        assertEquals(1L, user.companyId)
        assertTrue(user.isCompanyAdmin())
    }

    @Test
    fun `should detect company admin role correctly`() {
        val adminUser = User.create(
            username = "admin",
            email = "admin@company.com",
            firstName = "Admin",
            lastName = "User",
            password = "password123",
            roles = "ROLE_COMPANY_ADMIN"
        )

        val regularUser = User.create(
            username = "user",
            email = "user@company.com",
            firstName = "Regular",
            lastName = "User",
            password = "password123",
            roles = "ROLE_USER"
        )

        val userWithNoRole = User.create(
            username = "norole",
            email = "norole@company.com",
            firstName = "No",
            lastName = "Role",
            password = "password123"
        )

        assertTrue(adminUser.isCompanyAdmin())
        assertFalse(regularUser.isCompanyAdmin())
        assertFalse(userWithNoRole.isCompanyAdmin())
    }

    // Email validation integration tests
    @Test
    fun `should create user with valid email formats`() {
        val validEmails = listOf(
            "user@example.com",
            "test.email+tag@domain.co.uk",
            "user123@test-site.org",
            "first.last@subdomain.example.com",
            "user+filter@example.museum"
        )

        validEmails.forEach { emailStr ->
            val user = User.create(
                username = "testuser",
                email = emailStr,
                firstName = "Test",
                lastName = "User",
                password = "password123"
            )
            assertEquals(emailStr, user.email.value)
        }
    }

    @Test
    fun `should reject user creation with invalid email formats`() {
        val invalidEmails = listOf(
            "invalid-email",
            "user@",
            "@domain.com",
            "user@domain",
            "user name@domain.com",
            "",
            "user@@domain.com"
        )

        invalidEmails.forEach { invalidEmail ->
            assertThrows<IllegalArgumentException>("Should reject email: $invalidEmail") {
                User.create(
                    username = "testuser",
                    email = invalidEmail,
                    firstName = "Test",
                    lastName = "User",
                    password = "password123"
                )
            }
        }
    }

    @Test
    fun `should handle edge case email formats in user creation`() {
        // Minimal valid email
        val user1 = User.create(
            username = "user1",
            email = "a@b.co",
            firstName = "A",
            lastName = "B",
            password = "pass123"
        )
        assertEquals("a@b.co", user1.email.value)

        // Email with special characters
        val user2 = User.create(
            username = "user2",
            email = "user+test123@sub-domain.example-site.org",
            firstName = "User",
            lastName = "Test",
            password = "password"
        )
        assertEquals("user+test123@sub-domain.example-site.org", user2.email.value)
    }

    @Test
    fun `should preserve email case in user entity`() {
        val user = User.create(
            username = "testuser",
            email = "User.Name@Example.Com",
            firstName = "Test",
            lastName = "User",
            password = "password123"
        )

        // Email VO should preserve exact case as entered
        assertEquals("User.Name@Example.Com", user.email.value)
    }

    @Test
    fun `should create users with different roles`() {
        val roles = listOf(
            null,
            "ROLE_USER",
            "ROLE_ADMIN",
            "ROLE_COMPANY_ADMIN",
            "ROLE_MANAGER,ROLE_USER"
        )

        roles.forEachIndexed { index, role ->
            val user = User.create(
                username = "user$index",
                email = "user$index@example.com",
                firstName = "User",
                lastName = "$index",
                password = "password",
                roles = role
            )

            assertEquals(role, user.roles)
            assertEquals(role?.contains("ROLE_COMPANY_ADMIN") == true, user.isCompanyAdmin())
        }
    }

    @Test
    fun `should handle user creation with company assignment`() {
        val companiesIds = listOf(null, 1L, 999L, Long.MAX_VALUE)

        companiesIds.forEachIndexed { index, companyId ->
            val user = User.create(
                username = "user$index",
                email = "user$index@company.com",
                firstName = "Company",
                lastName = "User",
                password = "password",
                companyId = companyId
            )

            assertEquals(companyId, user.companyId)
        }
    }

    @Test
    fun `should create user with default active status`() {
        val user = User.create(
            username = "activeuser",
            email = "active@example.com",
            firstName = "Active",
            lastName = "User",
            password = "password123"
        )

        assertTrue(user.isActive)
    }

    @Test
    fun `should verify password correctly`() {
        val user = User.create(
            username = "passworduser",
            email = "active@example.com",
            firstName = "Active",
            lastName = "User",
            password = "Password123!@"
        )

        assertTrue(user.verifyPassword("Password123!@"))
        assertFalse(user.verifyPassword("WrongPassword"))
    }
}