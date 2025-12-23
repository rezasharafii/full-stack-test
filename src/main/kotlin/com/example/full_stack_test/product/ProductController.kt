package com.example.full_stack_test.product

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/")
    fun index(): String =
        "index"

    @GetMapping("/products")
    fun products(model: Model): String {
        model.addAttribute(
            "products", productService.getAllProductsForView()
        )
        return "fragments/product-table"
    }
}