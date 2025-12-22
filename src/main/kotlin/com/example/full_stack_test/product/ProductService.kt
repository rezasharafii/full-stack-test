package com.example.full_stack_test.product
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    fun getAllProducts(): List<ProductRow> =
        productRepository.findAll()
}