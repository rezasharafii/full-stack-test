package com.example.full_stack_test.product

import java.math.BigDecimal

data class VariantRow(
    val id: Long,
    val productId: Long,
    val sku: String?,
    val price: BigDecimal?
)