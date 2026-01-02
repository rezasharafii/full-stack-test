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
import java.math.BigDecimal

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    private fun addFilterAttributes(
        model: Model,
        title: String?,
        productType: String?,
        vendor: String?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?
    ) {
        model.addAttribute("filterTitle", title ?: "")
        model.addAttribute("filterType", productType ?: "")
        model.addAttribute("filterVendor", vendor ?: "")
        model.addAttribute("filterMinPrice", minPrice?.toPlainString() ?: "")
        model.addAttribute("filterMaxPrice", maxPrice?.toPlainString() ?: "")
    }

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
        model.addAttribute("tableEndpoint", "/products")
        addFilterAttributes(model, null, null, null, null, null)

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
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        model: Model
    ): String {
        val products = productService.searchProductsForView(title, sort, dir)
        model.addAttribute("products", products)
        model.addAttribute("page", 1)
        model.addAttribute("size", products.size.coerceAtLeast(1))
        model.addAttribute("totalPages", 1)
        model.addAttribute("totalItems", products.size)
        model.addAttribute("paginationEnabled", false)
        model.addAttribute("sort", sort)
        model.addAttribute("dir", dir)
        model.addAttribute("tableEndpoint", "/products/search")
        addFilterAttributes(model, title, null, null, null, null)
        return "fragments/product-table"
    }

    @GetMapping("/filters")
    fun filterProductsPage(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) productType: String?,
        @RequestParam(required = false) vendor: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        model: Model
    ): String {
        val products = productService.filterProductsForView(
            title = title,
            productType = productType,
            vendor = vendor,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sort = sort,
            dir = dir
        )
        val productTypes = productService.getProductTypesForFilter()

        model.addAttribute("products", products)
        model.addAttribute("page", 1)
        model.addAttribute("size", products.size.coerceAtLeast(1))
        model.addAttribute("totalPages", 1)
        model.addAttribute("totalItems", products.size)
        model.addAttribute("paginationEnabled", false)
        model.addAttribute("sort", sort)
        model.addAttribute("dir", dir)
        model.addAttribute("tableEndpoint", "/products/filters/table")
        model.addAttribute("productTypes", productTypes)
        addFilterAttributes(model, title, productType, vendor, minPrice, maxPrice)

        return "products-filter"
    }

    @GetMapping("/filters/table")
    fun filterProductsTable(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) productType: String?,
        @RequestParam(required = false) vendor: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        model: Model
    ): String {
        val products = productService.filterProductsForView(
            title = title,
            productType = productType,
            vendor = vendor,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sort = sort,
            dir = dir
        )

        model.addAttribute("products", products)
        model.addAttribute("page", 1)
        model.addAttribute("size", products.size.coerceAtLeast(1))
        model.addAttribute("totalPages", 1)
        model.addAttribute("totalItems", products.size)
        model.addAttribute("paginationEnabled", false)
        model.addAttribute("sort", sort)
        model.addAttribute("dir", dir)
        model.addAttribute("tableEndpoint", "/products/filters/table")
        addFilterAttributes(model, title, productType, vendor, minPrice, maxPrice)

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
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) productType: String?,
        @RequestParam(required = false) vendor: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false, defaultValue = "desc") dir: String,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        model: Model
    ): String {
        productService.deleteProduct(id)
        val hasFilters = !title.isNullOrBlank() ||
            !productType.isNullOrBlank() ||
            !vendor.isNullOrBlank() ||
            minPrice != null ||
            maxPrice != null

        return if (hasFilters) {
            filterProductsTable(title, productType, vendor, minPrice, maxPrice, sort, dir, model)
        } else {
            listProducts(sort, dir, page, size, model)
        }
    }

}
