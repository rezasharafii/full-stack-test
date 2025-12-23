package com.example.full_stack_test.product
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    fun getAllProducts(): List<ProductRow> =
        productRepository.findAll()


    fun getAllProductsForView(): List<ProductView> {
        val rows = productRepository.findAllWithVariants()

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
}