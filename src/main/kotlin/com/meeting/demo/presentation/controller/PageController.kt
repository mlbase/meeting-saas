package com.meeting.demo.presentation.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/page")
class PageController {
    
    @GetMapping("/signup")
    fun showSignupPage(): String {
        return "signup"
    }
    
    @GetMapping("/login")
    fun showLoginPage(): String {
        return "login"
    }
}