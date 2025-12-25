package com.example.full_stack_test.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    }


    fun getAllProducts(): List<ProductRow> =
        productRepository.findAll()


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


    fun searchProductsByTitle(title: String?): List<ProductRow> {
        return if (title.isNullOrBlank()) {
            productRepository.findAll()
        } else {
            productRepository.findByTitle(title.trim())
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


    fun getProductForUpdate(productId: Long): ProductView {
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

    @Transactional
    fun deleteProduct(productId: Long) {
        productRepository.deleteVariantsByProductId(productId)
        productRepository.deleteProductById(productId)
    }


}