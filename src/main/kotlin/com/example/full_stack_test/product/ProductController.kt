package com.example.full_stack_test.product

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/api/products")
    fun products(): List<ProductRow> =
        productService.getAllProducts()
}