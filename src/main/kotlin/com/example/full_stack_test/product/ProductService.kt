package com.example.full_stack_test.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ProductService(
    private val productRepository: ProductRepository
) {


    private fun mapToProductViews(rows: List<ProductVariantRow>): List<ProductView> {
        return rows
            .groupBy { it.productId }
            .map { (_, productRows) ->
                val first = productRows.first()

                ProductView(
                    id = first.productId,
                    title = first.title,
                    vendor = first.vendor,
                    productType = first.productType,
                    createdAt = first.createdAt,
                    variants = productRows
                        .filter { it.variantId != null }
                        .map {
                            VariantView(
                                id = it.variantId!!,
                                sku = it.sku!!,
                                price = it.price
                                    ?.setScale(2, RoundingMode.HALF_UP)
                                    ?.toPlainString()
                            )
                        }
                )
        }
    }

    fun getProductPageForView(
        sort: String = "createdAt",
        dir: String = "desc",
        page: Int = 1,
        size: Int = 10
    ): ProductPageView {
        val safeSize = size.coerceIn(1, 100)
        val totalItems = productRepository.countAllProducts().toInt()
        val totalPages = if (totalItems == 0) 1 else ((totalItems + safeSize - 1) / safeSize)
        val safePage = page.coerceIn(1, totalPages)
        val offset = (safePage - 1) * safeSize

        val productIds = productRepository.findProductIdsPage(
            sort = sort,
            dir = dir,
            limit = safeSize,
            offset = offset
        )
        val rows = productRepository.findByIdsWithVariants(productIds, sort, dir)

        return ProductPageView(
            products = mapToProductViews(rows),
            page = safePage,
            size = safeSize,
            totalItems = totalItems,
            totalPages = totalPages
        )
    }

    fun getAllProductsForView(
        sort: String = "createdAt",
        dir: String = "desc"
    ): List<ProductView> {
        val rows = productRepository.findAllWithVariantsSorted(sort, dir)
        return mapToProductViews(rows)
    }


    fun createProduct(form: ProductCreateForm) {
        val productId = productRepository.insertManualProduct(
            title = form.title,
            vendor = form.vendor,
            productType = form.productType
        )
        if (form.variants.isNotEmpty()) {
            form.variants
                .filterNotNull()
                .filter { it.sku != null && it.price != null }
                .forEach { variant ->
                    productRepository.insertVariant(
                        productId = productId,
                        sku = variant.sku,
                        price = variant.price
                    )
                }
        }
    }


    fun searchProductsForView(title: String?): List<ProductView> {
        val rows = if (title.isNullOrBlank()) {
            productRepository.findAllWithVariants()
        } else {
            productRepository.findByTitleWithVariants(title.trim())
        }

        return mapToProductViews(rows)
    }


    fun getProductById(productId: Long): ProductView {
        val rows = productRepository.findByIdWithVariants(productId)

        if (rows.isEmpty()) {
            throw IllegalArgumentException("Product not found")
        }

        val first = rows.first()

        return ProductView(
            id = first.productId,
            title = first.title,
            vendor = first.vendor,
            productType = first.productType,
            createdAt = first.createdAt,
            variants = rows
                .filter { it.sku != null }
                .map {
                    VariantView(
                        id = it.variantId,
                        sku = it.sku,
                        price = it.price
                            ?.setScale(2, RoundingMode.HALF_UP)
                            ?.toPlainString()
                    )
                }
        )
    }

    @Transactional
    fun updateProduct(productId: Long, form: ProductCreateForm) {

        /* -------------------------
         * 1. Update product
         * ------------------------- */
        productRepository.updateManualProduct(
            id = productId,
            title = form.title,
            vendor = form.vendor,
            productType = form.productType
        )

        /* -------------------------
         * 2. Remove existing variants
         * ------------------------- */
        productRepository.deleteVariantsByProductId(productId)

        /* -------------------------
         * 3. Reinsert submitted variants
         * ------------------------- */
        if (form.variants.isNotEmpty()) {
            form.variants
                .filterNotNull()
                .filter { !it.sku.isNullOrBlank() }
                .forEach { variant ->
                    productRepository.insertVariant(
                        productId = productId,
                        sku = variant.sku,
                        price = variant.price
                    )
                }
        }
    }

    @Transactional
    fun deleteProduct(productId: Long) {
        productRepository.deleteVariantsByProductId(productId)
        productRepository.deleteProductById(productId)
    }

    fun getVariantPrice(variantId: Long): BigDecimal {
        return productRepository.findVariantById(variantId)
            ?.price
            ?: throw IllegalStateException("Variant $variantId has no price")
    }
}
