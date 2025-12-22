package com.example.full_stack_test.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.simple.JdbcClient
import javax.sql.DataSource

@Configuration
class JdbcConfig {

    @Bean
    fun jdbcClient(dataSource: DataSource): JdbcClient =
        JdbcClient.create(dataSource)
}
