package com.example.full_stack_test.product

import org.springframework.stereotype.Service
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
                    title = first.title,
                    vendor = first.vendor,
                    productType = first.productType,
                    createdAt = first.createdAt,
                    variants = productRows
                        .filter { it.sku != null }
                        .map {
                            VariantView(
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


    fun getAllProductsForView(): List<ProductView> {
        val rows = productRepository.findAllWithVariants()
        return mapToProductViews(rows)
    }


    fun createProduct(form: ProductCreateForm) {
        val productId = productRepository.insertManualProduct(
            title = form.title,
            vendor = form.vendor,
            productType = form.productType
        )

        form.variants
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



}