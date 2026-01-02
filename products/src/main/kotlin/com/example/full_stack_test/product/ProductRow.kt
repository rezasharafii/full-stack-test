package com.example.full_stack_test.product

import java.time.OffsetDateTime

data class ProductRow(
    val id: Long,
    val externalId: Long,
    val title: String,
    val vendor: String?,
    val productType: String?,
    val createdAt: OffsetDateTime
)