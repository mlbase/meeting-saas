package com.meeting.demo.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {
    
    @PostConstruct
    fun setTimeZone() {
        // Set JVM timezone to UTC for consistent timestamp handling
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}