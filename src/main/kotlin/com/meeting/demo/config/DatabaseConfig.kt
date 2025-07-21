package com.meeting.demo.config

import org.jooq.DSLContext
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import javax.sql.DataSource

@Configuration
class DatabaseConfig {

    @Bean
    fun dslContext(dataSource: DataSource): DSLContext {
        val configuration = DefaultConfiguration()
        val connectionProvider = DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))
        configuration.set(connectionProvider)
        return DefaultDSLContext(configuration)
    }
}