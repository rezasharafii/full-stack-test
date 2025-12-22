package com.example.full_stack_test.product

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val jdbc: JdbcClient
) {

    fun findAll(): List<ProductRow> =
        jdbc.sql(
            """
            select
                id,
                external_id as externalId,
                title,
                vendor,
                product_type as productType,
                created_at as createdAt
            from products
            order by created_at desc
            """
        )
            .query(ProductRow::class.java)
            .list()
            .filterNotNull()
}
