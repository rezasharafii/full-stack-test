package com.example.full_stack_test.sale
import java.math.BigDecimal
import java.time.LocalDateTime

data class Sale(
    val id: Long? = null,
    val productId: Long,
    val variantId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val taxRate: BigDecimal,
    val taxAmount: BigDecimal,
    val totalAmount: BigDecimal,
    val paymentMethod: String = "CARD",
    val soldAt: LocalDateTime = LocalDateTime.now()
)

