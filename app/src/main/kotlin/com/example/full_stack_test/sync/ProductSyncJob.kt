package com.example.full_stack_test.sync

import com.example.full_stack_test.product.ProductRepository
import com.example.full_stack_test.sale.SaleRepository
import com.example.full_stack_test.sale.SaleService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductSyncJob(
    private val fammeClient: FammeClient,
    private val productRepository: ProductRepository,
    private val accountingService: SaleService,
    private val saleRepository: SaleRepository

) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(initialDelay = 0, fixedDelay = 24 * 60 * 60 * 1000)
    @Transactional
    fun syncProducts() {
        log.info("Starting product sync from Famme")

        val products = fammeClient.fetchProducts()
            .products
            .take(50)

        productRepository.deleteAll()
        saleRepository.deleteAll()

        products.forEach { product ->
            val productId = productRepository.insertProduct(
                externalId = product.id,
                title = product.title,
                vendor = product.vendor,
                productType = product.product_type
            )

            product.variants.forEach { variant ->
                val variantId =  productRepository.insertVariant(
                    productId = productId,
                    sku = variant.sku,
                    price = variant.price
                )


                accountingService.recordSale(
                    productId = productId,
                    variantId = variantId,
                    price = variant.price
                )
            }
        }

        log.info("Product sync completed. Saved ${products.size} products.")
    }
}