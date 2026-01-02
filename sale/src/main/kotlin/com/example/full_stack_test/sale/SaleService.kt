package com.example.full_stack_test.sale

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class SaleService(private val saleRepository: SaleRepository) {

    private val taxRate = BigDecimal("0.15")

    @Transactional
    fun recordSale(productId: Long, variantId: Long, price: BigDecimal?, quantity: Int = 1): Sale? {
        if (price == null) return null

        val taxAmount = price.multiply(taxRate).setScale(2, RoundingMode.HALF_UP)
        val totalAmount = price.add(taxAmount).multiply(BigDecimal(quantity))

        val sale = Sale(
            productId = productId,
            variantId = variantId,
            quantity = quantity,
            unitPrice = price,
            taxRate = taxRate,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            paymentMethod = "CARD",
            soldAt = LocalDateTime.now()
        )

        val savedSaleId = saleRepository.save(sale)

        val savedSale = sale.copy(id = savedSaleId)
        return savedSale
    }


    fun getMonthlySummary(productTitle: String? = null): AccountingSummary {
        val totals = saleRepository.getMonthlyFinancialTotals(productTitle)
        val recentSales = saleRepository.findRecentSaleRows(productTitle)

        return AccountingSummary(
            totalRevenue = totals.revenue,
            totalTax = totals.tax,
            salesCount = totals.count,
            sales = recentSales
        )
    }

}
