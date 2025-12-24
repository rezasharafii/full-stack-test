package com.example.full_stack_test.product

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {


    @GetMapping
    fun listProducts(
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        model: Model
    ): String {
        val products = productService.getAllProductsForView(sort, dir)

        model.addAttribute("products", products)
        model.addAttribute("sort", sort)
        model.addAttribute("dir", dir)

        return "fragments/product-table"
    }

    @GetMapping("/variant-row")
    fun getVariantRowFragment(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(required = false) sku: String?,
        @RequestParam(required = false) price: String?,
        model: Model
    ): String {
        model.addAttribute("index", index)
        model.addAttribute("sku", sku)
        model.addAttribute("price", price)
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


    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) title: String?,
        model: Model
    ): String {
        val products = productService.searchProductsForView(title)
        model.addAttribute("products", products)
        return "fragments/product-table"
    }

    @GetMapping("/{id}/edit")
    fun editProductPage(
        @PathVariable id: Long,
        model: Model
    ): String {
        val product = productService.getProductForUpdate(id)
        model.addAttribute("product", product)
        return "/edit"
    }

    @PutMapping("/{id}/edit")
    fun updateProduct(
        @PathVariable id: Long,
        @ModelAttribute form: ProductCreateForm,
        model: Model
    ): ResponseEntity<Unit> {
        productService.updateProduct(id, form)

        val products = productService.getAllProductsForView()
        model.addAttribute("products", products)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
        model: Model
    ): String {

        productService.deleteProduct(id)

        val products = productService.getAllProductsForView()
        model.addAttribute("products", products)

        return "fragments/product-table"
    }

}
