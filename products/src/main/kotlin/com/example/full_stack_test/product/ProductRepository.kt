package com.example.full_stack_test.product

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import kotlin.random.Random

@Repository
class ProductRepository(
    private val jdbc: JdbcClient
) {

    private fun resolveSortColumn(sort: String): String =
        when (sort) {
            "title" -> "p.title"
            "vendor" -> "p.vendor"
            "type" -> "p.product_type"
            "createdAt" -> "p.created_at"
            else -> "p.created_at"
        }

    private fun resolveSortDirection(dir: String): String =
        if (dir.equals("asc", true)) "asc" else "desc"


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
    ): Long =
        jdbc.sql(
            """
            insert into variants (product_id, sku, price)
            values (:productId, :sku, :price)
            returning id
            """
        )
            .param("productId", productId)
            .param("sku", sku)
            .param("price", price)
            .query(Long::class.java)
            .single()


    fun findAllWithVariants(): List<ProductVariantRow> =
        jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        order by p.created_at desc
        """
        )
            .query(ProductVariantRow::class.java)
            .list().filterNotNull()

    fun countAllProducts(): Long =
        jdbc.sql(
            """
        select count(*) from products
        """
        )
            .query(Long::class.java)
            .single()

    fun findProductIdsPage(
        sort: String,
        dir: String,
        limit: Int,
        offset: Int
    ): List<Long> {
        val sortColumn = resolveSortColumn(sort)
        val direction = resolveSortDirection(dir)

        return jdbc.sql(
            """
        select
            p.id
        from products p
        order by $sortColumn $direction, p.id $direction
        limit :limit
        offset :offset
        """
        )
            .param("limit", limit)
            .param("offset", offset)
            .query(Long::class.java)
            .list()
            .filterNotNull()
    }

    fun findByIdsWithVariants(
        productIds: List<Long>,
        sort: String,
        dir: String
    ): List<ProductVariantRow> {
        if (productIds.isEmpty()) {
            return emptyList()
        }

        val sortColumn = resolveSortColumn(sort)
        val direction = resolveSortDirection(dir)

        return jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        where p.id in (:productIds)
        order by $sortColumn $direction, p.id $direction
        """
        )
            .param("productIds", productIds)
            .query(ProductVariantRow::class.java)
            .list()
            .filterNotNull()
    }


    fun insertManualProduct(
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
            .param("externalId", Random.nextInt()) // simple unique id
            .param("title", title)
            .param("vendor", vendor)
            .param("productType", productType)
            .query(Long::class.java)
            .single()



    fun findByTitleWithVariants(title: String): List<ProductVariantRow> =
        jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        where p.title ilike :title
        order by p.created_at desc
        """
        )
            .param("title", "%$title%")
            .query(ProductVariantRow::class.java)
            .list()
            .filterNotNull()

    fun findByTitleWithVariantsSorted(title: String, sort: String, dir: String): List<ProductVariantRow> {
        val sortColumn = resolveSortColumn(sort)
        val direction = resolveSortDirection(dir)

        return jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        where p.title ilike :title
        order by $sortColumn $direction, p.id $direction
        """
        )
            .param("title", "%$title%")
            .query(ProductVariantRow::class.java)
            .list()
            .filterNotNull()
    }


    fun findByIdWithVariants(productId: Long): List<ProductVariantRow> =
        jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        where p.id = :productId
        """
        )
            .param("productId", productId)
            .query(ProductVariantRow::class.java)
            .list()
            .filterNotNull()

    fun updateManualProduct(
        id: Long,
        title: String,
        vendor: String?,
        productType: String?
    ) {
        jdbc.sql(
            """
        update products
        set
            title = :title,
            vendor = :vendor,
            product_type = :productType
        where id = :id
        """
        )
            .param("id", id)
            .param("title", title)
            .param("vendor", vendor)
            .param("productType", productType)
            .update()
    }

    fun deleteVariantsByProductId(productId: Long) {
        jdbc.sql(
            """
        delete from variants
        where product_id = :productId
        """
        )
            .param("productId", productId)
            .update()
    }

    fun deleteProductById(productId: Long) {
        jdbc.sql(
            """
        delete from products
        where id = :productId
        """
        )
            .param("productId", productId)
            .update()
    }


    fun findAllWithVariantsSorted(sort: String, dir: String): List<ProductVariantRow> {
        val sortColumn = resolveSortColumn(sort)
        val direction = resolveSortDirection(dir)

        return jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        order by $sortColumn $direction, p.id $direction
        """
        )
            .query(ProductVariantRow::class.java)
            .list()
            .filterNotNull()
    }

    fun findFilteredWithVariants(
        title: String?,
        productType: String?,
        vendor: String?,
        minPrice: java.math.BigDecimal?,
        maxPrice: java.math.BigDecimal?,
        sort: String,
        dir: String
    ): List<ProductVariantRow> {
        val sortColumn = resolveSortColumn(sort)
        val direction = resolveSortDirection(dir)

        return jdbc.sql(
            """
        select
            p.id as productId,
            p.title,
            p.vendor,
            p.product_type as productType,
            p.created_at as createdAt,
            v.id as variantId,
            v.sku,
            v.price
        from products p
        left join variants v on v.product_id = p.id
        where (cast(:title as text) is null or p.title ilike cast(:title as text))
          and (cast(:productType as text) is null or p.product_type ilike cast(:productType as text))
          and (cast(:vendor as text) is null or p.vendor ilike cast(:vendor as text))
          and (cast(:minPrice as numeric) is null or v.price >= cast(:minPrice as numeric))
          and (cast(:maxPrice as numeric) is null or v.price <= cast(:maxPrice as numeric))
        order by $sortColumn $direction, p.id $direction
        """
        )
            .param("title", title?.let { "%$it%" })
            .param("productType", productType?.let { "%$it%" })
            .param("vendor", vendor?.let { "%$it%" })
            .param("minPrice", minPrice)
            .param("maxPrice", maxPrice)
            .query(ProductVariantRow::class.java)
            .list()
            .filterNotNull()
    }

    fun findDistinctProductTypes(): List<String> =
        jdbc.sql(
            """
        select distinct product_type
        from products
        where product_type is not null and product_type <> ''
        order by product_type
        """
        )
            .query(String::class.java)
            .list()
            .filterNotNull()


    fun findVariantById(variantId: Long): VariantRow? = jdbc.sql(
        """
            select
               *
            from variants
            where id = :variantId
            """
    )
        .param("variantId", variantId)
        .query(VariantRow::class.java).single()

}
