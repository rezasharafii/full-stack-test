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

data class ProductPageView(
    val products: List<ProductView>,
    val page: Int,
    val size: Int,
    val totalItems: Int,
    val totalPages: Int
)
