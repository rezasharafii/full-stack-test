package com.example.full_stack_test.product

import java.math.BigDecimal

data class VariantCreateForm(
    val sku: String?,
    val price: BigDecimal?
)