package com.meeting.demo.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserStatusTest {

    private fun 유저_생성(roles: String? = null) = User.create(
        username = "testuser",
        email = "test@example.com",
        firstName = "Test",
        lastName = "User",
        password = "password123",
        roles = roles
    )

    // ==============================
    // UserStatus 기본값
    // ==============================

    @Test
    fun `유저의 기본 상태는 AVAILABLE이다`() {
        val user = 유저_생성()
        assertEquals(UserStatus.AVAILABLE, user.status)
    }

    // ==============================
    // 상태 전이 — 정상
    // ==============================

    @Test
    fun `AVAILABLE 상태에서 PLANNING으로 전환할 수 있다`() {
        val user = 유저_생성()

        user.changeStatus(UserStatus.PLANNING)

        assertEquals(UserStatus.PLANNING, user.status)
    }

    @Test
    fun `PLANNING 상태에서 WORKING으로 전환할 수 있다`() {
        val user = 유저_생성()
        user.changeStatus(UserStatus.PLANNING)

        user.changeStatus(UserStatus.WORKING)

        assertEquals(UserStatus.WORKING, user.status)
    }

    @Test
    fun `WORKING 상태에서 AVAILABLE로 전환할 수 있다`() {
        val user = 유저_생성()
        user.changeStatus(UserStatus.PLANNING)
        user.changeStatus(UserStatus.WORKING)

        user.changeStatus(UserStatus.AVAILABLE)

        assertEquals(UserStatus.AVAILABLE, user.status)
    }

    // ==============================
    // 상태 전이 — 불가
    // ==============================

    @Test
    fun `AVAILABLE 상태에서 WORKING으로 직접 전환할 수 없다`() {
        val user = 유저_생성()

        assertThrows<IllegalStateException> {
            user.changeStatus(UserStatus.WORKING)
        }
    }

    @Test
    fun `WORKING 상태에서 PLANNING으로 직접 전환할 수 없다`() {
        val user = 유저_생성()
        user.changeStatus(UserStatus.PLANNING)
        user.changeStatus(UserStatus.WORKING)

        assertThrows<IllegalStateException> {
            user.changeStatus(UserStatus.PLANNING)
        }
    }

    @Test
    fun `PLANNING 상태에서 AVAILABLE로 직접 전환할 수 없다`() {
        val user = 유저_생성()
        user.changeStatus(UserStatus.PLANNING)

        assertThrows<IllegalStateException> {
            user.changeStatus(UserStatus.AVAILABLE)
        }
    }

    // ==============================
    // C-level 역할 체크
    // ==============================

    @Test
    fun `C_LEVEL 역할을 가진 유저는 isCLevel이 true이다`() {
        val user = 유저_생성(roles = "C_LEVEL")
        assertTrue(user.isCLevel())
    }

    @Test
    fun `MANAGER 역할을 가진 유저는 isCLevel이 false이다`() {
        val user = 유저_생성(roles = "MANAGER")
        assertFalse(user.isCLevel())
    }

    @Test
    fun `역할이 없는 유저는 isCLevel이 false이다`() {
        val user = 유저_생성()
        assertFalse(user.isCLevel())
    }

    // ==============================
    // PLANNING 상태 체크
    // ==============================

    @Test
    fun `PLANNING 상태인 유저는 isPlanning이 true이다`() {
        val user = 유저_생성()
        user.changeStatus(UserStatus.PLANNING)

        assertTrue(user.isPlanning())
    }

    @Test
    fun `AVAILABLE 상태인 유저는 isPlanning이 false이다`() {
        val user = 유저_생성()
        assertFalse(user.isPlanning())
    }
}
