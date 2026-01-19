package com.balaeventos.baladaeventos.payments.manager.adapter.input.web.controller

import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment")
class PaymentController {

    @PostMapping
    fun processPayment(@RequestBody request : Map<String, Any>) : ResponseEntity<Map<String, Any>> {

        val logger = LoggerFactory.getLogger(PaymentController::class.java)

        val amount: Long = when (val raw = request["amount"]) {
            is Number -> raw.toLong()
            is String -> raw.toLongOrNull() ?: throw IllegalArgumentException("amount inválido")
            null -> throw IllegalArgumentException("amount ausente")
            else -> throw IllegalArgumentException("tipo de amount inválido")
        }

        val currency = request["currency"] as? String ?: throw IllegalArgumentException("currency ausente ou inválido")

        val params = PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setCurrency(currency)
            .build()

        val intent = PaymentIntent.create(params)

        logger.info("Created PaymentIntent with id: ${intent.id}, amount: ${intent.amount}, currency: ${intent.currency}")

        return ResponseEntity.ok(
            mapOf(
                "clientSecret" to intent.clientSecret
            )
        )

    }
}