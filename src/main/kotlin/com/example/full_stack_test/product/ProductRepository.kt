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


    fun deleteAll() {
        jdbc.sql("delete from products").update()
    }

    fun insertProduct(
        externalId: Long,
        title: String,
        vendor: String?,
        productType: String?
    ): Long =
        jdbc.sql(
            """
            insert into products (external_id, title, vendor, product_type)
            values (:externalId, :title, :vendor, :productType)
            returning id
            """
        )
            .param("externalId", externalId)
            .param("title", title)
            .param("vendor", vendor)
            .param("productType", productType)
            .query(Long::class.java)
            .single()

    fun insertVariant(
        productId: Long,
        sku: String?,
        price: java.math.BigDecimal?
    ) {
        jdbc.sql(
            """
            insert into variants (product_id, sku, price)
            values (:productId, :sku, :price)
            """
        )
            .param("productId", productId)
            .param("sku", sku)
            .param("price", price)
            .update()
    }


    fun findAllWithVariants(): List<ProductVariantRow> =
        jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        order by p.created_at desc
        """
        )
            .query(ProductVariantRow::class.java)
            .list().filterNotNull()

}
