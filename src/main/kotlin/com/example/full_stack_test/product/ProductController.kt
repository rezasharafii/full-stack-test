package com.example.full_stack_test.product

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/view")
    fun viewDashboard(): String = "index"

    @GetMapping
    fun listProducts(model: Model): String {
        val products = productService.getAllProductsForView()
        model.addAttribute("products", products)
        return "fragments/product-table"
    }

    @GetMapping("/variant-row")
    fun getVariantRowFragment(
        @RequestParam(defaultValue = "0") index: Int,
        model: Model
    ): String {
        model.addAttribute("index", index)
        return "fragments/variant-row"
    }

    @PostMapping
    fun createProduct(
        @ModelAttribute form: ProductCreateForm,
        model: Model
    ): String {
        productService.createProduct(form)

        model.addAttribute("products", productService.getAllProductsForView())

        return "fragments/product-table"
    }
}
