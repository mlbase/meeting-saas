package com.meeting.demo.service

import com.meeting.demo.presentation.dto.UserDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Service

/**
 * Service class for managing user sessions stored in Redis.
 */
@Service
class SessionService {

    companion object {
        private const val USER_SESSION_KEY = "authenticated_user"
        private const val LOGIN_TIME_KEY = "login_time"
    }

    /**
     * Store user information in the session after successful login.
     */
    fun createUserSession(request: HttpServletRequest, user: UserDto): String {
        val session: HttpSession = request.getSession(true)
        
        // Store user information in session
        session.setAttribute(USER_SESSION_KEY, user)
        session.setAttribute(LOGIN_TIME_KEY, System.currentTimeMillis())
        
        // Return session ID for client to use in subsequent requests
        return session.id
    }

    /**
     * Retrieve user information from the session.
     */
    fun getUserFromSession(request: HttpServletRequest): UserDto? {
        val session: HttpSession? = request.getSession(false)
        return session?.getAttribute(USER_SESSION_KEY) as? UserDto
    }

    /**
     * Check if the current session has an authenticated user.
     */
    fun isUserAuthenticated(request: HttpServletRequest): Boolean {
        return getUserFromSession(request) != null
    }

    /**
     * Invalidate the user session (logout).
     */
    fun invalidateSession(request: HttpServletRequest): Boolean {
        val session: HttpSession? = request.getSession(false)
        return if (session != null) {
            session.invalidate()
            true
        } else {
            false
        }
    }

    /**
     * Get the login time for the current session.
     */
    fun getLoginTime(request: HttpServletRequest): Long? {
        val session: HttpSession? = request.getSession(false)
        return session?.getAttribute(LOGIN_TIME_KEY) as? Long
    }

    /**
     * Update the last access time for the session (extends session expiry).
     */
    fun touchSession(request: HttpServletRequest) {
        val session: HttpSession? = request.getSession(false)
        session?.let {
            // Simply accessing the session updates its last access time
            it.lastAccessedTime
        }
    }
}