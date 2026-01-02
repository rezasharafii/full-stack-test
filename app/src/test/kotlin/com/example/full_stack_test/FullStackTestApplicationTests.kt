package com.example.full_stack_test

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan

@SpringBootTest
@ComponentScan(basePackages = ["com.example.full_stack_test"])
class FullStackTestApplicationTests {

    @Test
    fun contextLoads() {
    }

}

