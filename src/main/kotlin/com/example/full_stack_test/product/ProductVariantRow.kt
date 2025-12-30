package com.example.full_stack_test.product

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ProductVariantRow(
    val productId: Long,
    val title: String,
    val vendor: String?,
    val productType: String?,
    val createdAt: OffsetDateTime,
    val sku: String?,
    val price: BigDecimal?,
    val variantId : Long?
)