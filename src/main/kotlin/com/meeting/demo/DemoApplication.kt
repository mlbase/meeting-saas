package com.meeting.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

@SpringBootApplication
class DemoApplication {
    
    @Bean
    fun dispatcherServletRegistrationBean(): DispatcherServletRegistrationBean {
        // Create servlet context for page handling
        val servletContext = AnnotationConfigWebApplicationContext()
        servletContext.scan("com.meeting.demo.controller")
        
        // Create dispatcher servlet with context
        val dispatcherServlet = DispatcherServlet(servletContext)
        
        // Register with specific path mapping for pages
        val registration = DispatcherServletRegistrationBean(dispatcherServlet, "/page/*")
        registration.setName("pageDispatcher")
        registration.setLoadOnStartup(2) // Load after main dispatcher (which is 1)
        
        return registration
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
