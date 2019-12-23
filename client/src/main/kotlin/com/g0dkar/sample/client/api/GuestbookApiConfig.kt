package com.g0dkar.sample.client.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@Component
@ConstructorBinding
@ConfigurationProperties("guestbookApi")
data class GuestbookApiConfig(
    val url: String = "http://localhost:8080"
)
