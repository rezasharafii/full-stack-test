package com.example.full_stack_test.sale

import java.math.BigDecimal

data class AccountingSummary(
    val totalRevenue: BigDecimal = BigDecimal.ZERO,
    val totalTax: BigDecimal = BigDecimal.ZERO,
    val salesCount: Int = 0,
    val sales: List<SaleRow> = emptyList()
)

data class SaleRow(
    val id: Long,
    val productTitle: String,
    val sku: String,
    val totalAmount: BigDecimal,
    val soldAt: java.time.LocalDateTime,
    val quantity: Int
)

data class MonthlyTotals(
    val revenue: BigDecimal,
    val tax: BigDecimal,
    val count: Int
)
