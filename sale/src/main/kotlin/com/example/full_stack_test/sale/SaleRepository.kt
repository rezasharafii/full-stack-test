package com.example.full_stack_test.sale

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SaleRepository(
    private val jdbc: JdbcClient
) {

    fun save(sale: Sale): Long {
        val keyHolder = GeneratedKeyHolder()

        jdbc.sql(
            """
            INSERT INTO sales (product_id, variant_id, quantity, unit_price, tax_rate, tax_amount, total_amount, payment_method, sold_at)
            VALUES (:productId, :variantId, :quantity, :unitPrice, :taxRate, :taxAmount, :totalAmount, :paymentMethod, :soldAt)
        """
        )
            .param("productId", sale.productId)
            .param("variantId", sale.variantId)
            .param("quantity", sale.quantity)
            .param("unitPrice", sale.unitPrice)
            .param("taxRate", sale.taxRate)
            .param("taxAmount", sale.taxAmount)
            .param("totalAmount", sale.totalAmount)
            .param("paymentMethod", sale.paymentMethod)
            .param("soldAt", sale.soldAt)
            .update(keyHolder, "id") // This specifies the column name to return

        return keyHolder.key?.toLong() ?: throw IllegalStateException("Failed to insert sale, no ID returned.")
    }


    fun deleteAll() {
        jdbc.sql("delete from sales").update()
    }

    fun findRecentSaleRows(): List<SaleRow> {
        return jdbc.sql(
            """
            SELECT s.id, p.title as productTitle, v.sku, s.total_amount as totalAmount, s.sold_at as soldAt , quantity
            FROM sales s
            JOIN products p ON s.product_id = p.id
            JOIN variants v ON s.variant_id = v.id
            ORDER BY s.sold_at DESC 
        """
        )
            .query(SaleRow::class.java)
            .list().filterNotNull()
    }



    fun getMonthlyFinancialTotals(): MonthlyTotals {
        return jdbc.sql("""
            SELECT 
                COALESCE(SUM(total_amount), 0) as totalRevenue, 
                COALESCE(SUM(tax_amount), 0) as totalTax, 
                COUNT(*) as salesCount 
            FROM sales 
            WHERE sold_at >= date_trunc('month', CURRENT_DATE)
        """)
            .query { rs, _ ->
                MonthlyTotals(
                    revenue = rs.getBigDecimal("totalRevenue"),
                    tax = rs.getBigDecimal("totalTax"),
                    count = rs.getInt("salesCount")
                )
            }
            .single()
    }
}
