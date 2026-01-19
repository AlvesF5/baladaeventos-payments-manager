package com.balaeventos.baladaeventos.payments.manager.application.config

import com.stripe.Stripe
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class PaymentConfig(
    @Value("\${STRIPE_SECRET_KEY}") val stripeSecretKey: String
) {

    @PostConstruct
    fun initKey() {
       Stripe.apiKey = stripeSecretKey
    }
}