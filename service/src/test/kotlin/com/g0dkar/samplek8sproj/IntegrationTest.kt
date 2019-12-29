package com.g0dkar.samplek8sproj

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [IntegrationTestContext::class], webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
internal abstract class IntegrationTest {
    @LocalServerPort
    protected var serverPort: Int = 0

    @BeforeAll
    fun beforeAll() {
        RestAssured.baseURI = "http://localhost:$serverPort"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }
}
