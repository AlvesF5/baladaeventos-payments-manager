package com.balaeventos.baladaeventos.payments.manager.adapter.input.web.controller

import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/webhooks/stripe")
class WebhookController(
    @Value("\${STRIPE_WEBHOOK_SECRET}")
    private val webhookSecret: String
) {

    @PostMapping
    fun handleWebhookEvent(
        request: HttpServletRequest,
        @RequestBody payload: String,
        @RequestHeader("Stripe-Signature") header: String
    ): String {
        val event: Event = try {
            Webhook.constructEvent(payload, header, webhookSecret)
        } catch (e: SignatureVerificationException) {
            // invalid signature
            return "Signature verification failed"
        }

        when (event.type) {
            "payment_intent.succeeded" -> {
                val intent = event.dataObjectDeserializer.`object`.orElse(null) as? PaymentIntent
                intent?.let {
                    println("✅ PaymentIntent succeeded: ${it.id} amount=${it.amount}")
                    // TODO: update your DB (mark order paid)
                }
            }
            "payment_intent.payment_failed" -> {
                val failedIntent = event.dataObjectDeserializer.`object`.orElse(null) as? PaymentIntent
                failedIntent?.let {
                    println("❌ PaymentIntent failed: ${it.id}")
                    // TODO: update your DB (mark order failed)
                }
            }
            else -> {
                println("Unhandled event type: ${event.type}")
            }
        }

        return "ok"
    }
}