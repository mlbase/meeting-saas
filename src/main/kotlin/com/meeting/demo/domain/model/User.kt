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
    var password: Password,
    
    @Embedded
    var email: Email,
    
    @Column(name = "first_name")
    var firstName: String,
    
    @Column(name = "last_name")
    var lastName: String,
    
    
    @Column(name = "is_active")
    var isActive: Boolean = true,
    
    var roles: String? = null,

    var status: UserStatus = UserStatus.AVAILABLE,
    
    @Column(name = "company_id")
    val companyId: Long? = null
) : BaseEntity() {
    fun isCompanyAdmin(): Boolean = roles?.contains("ROLE_COMPANY_ADMIN") ?: false
    fun isCLevel(): Boolean = roles?.contains("C_LEVEL") ?: false
    fun isPlanning(): Boolean = status == UserStatus.PLANNING
    
    /**
     * Verify if the given plain text password matches the user's password
     */
    fun verifyPassword(plainTextPassword: String): Boolean {
        return password.matches(plainTextPassword)
    }

    fun changeStatus(newStatus: UserStatus) {
        val allowed = mapOf(
            UserStatus.AVAILABLE to setOf(UserStatus.PLANNING),
            UserStatus.PLANNING  to setOf(UserStatus.WORKING),
            UserStatus.WORKING   to setOf(UserStatus.AVAILABLE)
        )
        if (newStatus !in (allowed[status] ?: emptySet()))
            throw IllegalStateException("$status 상태에서 $newStatus 로 전환할 수 없습니다.")
        status = newStatus
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

enum class UserRole {
    USER, C_LEVEL, ADMIN
}

enum class UserStatus {
    PLANNING, WORKING, AVAILABLE, PTO, EXPIRED
}