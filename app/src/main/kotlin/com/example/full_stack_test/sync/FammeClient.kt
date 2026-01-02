package com.example.full_stack_test.sync

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class FammeClient(
    private val restClient: RestClient
) {

    fun fetchProducts(): FammeResponse =
        restClient.get()
            .uri("/products.json")
            .retrieve()
            .body<FammeResponse>()
            ?: throw IllegalStateException("Failed to fetch products from Famme")
}