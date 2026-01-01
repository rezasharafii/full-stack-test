package com.example.full_stack_test.controller

import com.example.full_stack_test.product.ProductService
import com.example.full_stack_test.sale.SaleService
import com.example.full_stack_test.sale.SalesStream
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Controller
@RequestMapping("/sales")
class SaleController(
    val saleService: SaleService, private val salesStream: SalesStream,
    private val productService: ProductService
) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("summary", saleService.getMonthlySummary())
        return "sales"
    }


    @GetMapping("/stream")
    fun stream(model: Model): SseEmitter {
        val emitter = SseEmitter(0)
        salesStream.register(emitter)
        return emitter
    }

    @GetMapping("/new")
    fun newSaleDialog(
        @RequestParam productId: Long,
        model: Model
    ): String {

        val product = productService.getProductById(productId)

        model.addAttribute("product", product)

        return "fragments/create-sale-dialog"
    }


    @PostMapping
    fun createSale(
        @RequestParam productId: Long,
        @RequestParam variantId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<Void> {

        saleService.recordSale(
            productId = productId,
            variantId = variantId,
            price = productService.getVariantPrice(variantId),
            quantity = quantity
        )

        salesStream.broadcastUpdate()

        return ResponseEntity.ok().build()
    }

}