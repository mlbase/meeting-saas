package com.meeting.demo.domain.model

import com.meeting.demo.domain.vo.Email
import com.meeting.demo.domain.vo.Password
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(unique = true, nullable = false)
    val username: String,
    
    @Embedded
    val password: Password,
    
    @Embedded
    val email: Email,
    
    @Column(name = "first_name")
    val firstName: String,
    
    @Column(name = "last_name")
    val lastName: String,
    
    
    @Column(name = "is_active")
    val isActive: Boolean = true,
    
    val roles: String? = null,
    
    @Column(name = "company_id")
    val companyId: Long? = null
) : BaseEntity() {
    fun isCompanyAdmin(): Boolean {
        return roles?.contains("ROLE_COMPANY_ADMIN") ?: false
    }
    
    /**
     * Verify if the given plain text password matches the user's password
     */
    fun verifyPassword(plainTextPassword: String): Boolean {
        return password.matches(plainTextPassword)
    }

    companion object {
        fun create(
            username: String,
            email: String,
            firstName: String,
            lastName: String,
            password: String,
            roles: String? = null,
            companyId: Long? = null
        ): User {
            return User(
                username = username,
                email = Email(email),
                firstName = firstName,
                lastName = lastName,
                password = Password.create(password),
                roles = roles,
                companyId = companyId
            )
        }
    }
}