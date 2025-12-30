package com.example.full_stack_test.sync

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeResponse(
    val products: List<FammeProduct>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeProduct(
    val id: Long,
    val title: String,
    val vendor: String?,
    val product_type: String?,
    val variants: List<FammeVariant>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FammeVariant(
    val id: Long,
    val sku: String?,
    val price: BigDecimal?
)