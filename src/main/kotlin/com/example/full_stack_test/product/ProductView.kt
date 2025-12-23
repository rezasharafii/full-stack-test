package com.example.full_stack_test.product

import java.time.OffsetDateTime

data class ProductView(
    val title: String,
    val vendor: String?,
    val productType: String?,
    val createdAt: OffsetDateTime,
    val variants: List<VariantView>
)

data class VariantView(
    val sku: String?,
    val price: String?
)