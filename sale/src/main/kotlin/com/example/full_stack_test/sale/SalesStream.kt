package com.example.full_stack_test.sale

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.util.concurrent.CopyOnWriteArrayList


@Component
class SalesStream(
    private val saleService: SaleService,
    private val templateEngine: SpringTemplateEngine
) {

    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun register(emitter: SseEmitter) {
        emitters += emitter
        emitter.onCompletion { emitters -= emitter }
        emitter.onTimeout { emitters -= emitter }

        pushUpdate(emitter)
    }

    fun broadcastUpdate() {
        emitters.forEach { pushUpdate(it) }
    }

    private fun pushUpdate(emitter: SseEmitter) {
        try {
            val context = Context()
            context.setVariable("summary", saleService.getMonthlySummary())
            val selectors = mutableSetOf<String?>("accounting-table")


            val html = templateEngine.process("sales", selectors, context)


            emitter.send(
                SseEmitter.event()
                    .data(html)
            )
        } catch (ex: Exception) {
            emitters -= emitter
        }
    }
}
