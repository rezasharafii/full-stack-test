package com.example.full_stack_test.controller

import com.example.full_stack_test.product.ProductCreateForm
import com.example.full_stack_test.product.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun listProducts(
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        model: Model
    ): String {
        val productPage = productService.getProductPageForView(sort, dir, page, size)

        model.addAttribute("products", productPage.products)
        model.addAttribute("page", productPage.page)
        model.addAttribute("size", productPage.size)
        model.addAttribute("totalPages", productPage.totalPages)
        model.addAttribute("totalItems", productPage.totalItems)
        model.addAttribute("paginationEnabled", true)
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
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        model: Model
    ): String {
        productService.createProduct(form)
        return listProducts(sort, dir, page, size, model)
    }


    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) title: String?,
        model: Model
    ): String {
        val products = productService.searchProductsForView(title)
        model.addAttribute("products", products)
        model.addAttribute("page", 1)
        model.addAttribute("size", products.size.coerceAtLeast(1))
        model.addAttribute("totalPages", 1)
        model.addAttribute("totalItems", products.size)
        model.addAttribute("paginationEnabled", false)
        model.addAttribute("sort", "createdAt")
        model.addAttribute("dir", "desc")
        return "fragments/product-table"
    }

    @GetMapping("/{id}/edit")
    fun editProductPage(
        @PathVariable id: Long,
        model: Model
    ): String {
        val product = productService.getProductById(id)
        model.addAttribute("product", product)
        return "/edit"
    }

    @PutMapping("/{id}/edit")
    fun updateProduct(
        @PathVariable id: Long,
        @ModelAttribute form: ProductCreateForm
    ): ResponseEntity<Unit> {
        productService.updateProduct(id, form)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        model: Model
    ): String {
        productService.deleteProduct(id)
        return listProducts(sort, dir, page, size, model)
    }

}