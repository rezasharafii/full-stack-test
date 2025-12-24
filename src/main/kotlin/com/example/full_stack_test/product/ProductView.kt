package com.example.full_stack_test.product

import java.time.OffsetDateTime

data class ProductView(
    val id: Long,
    val title: String,
    val vendor: String?,
    val productType: String?,
    val createdAt: OffsetDateTime,
    val variants: List<VariantView>
)

data class VariantView(
    val id: Long?,
    val sku: String?,
    val price: String?
)