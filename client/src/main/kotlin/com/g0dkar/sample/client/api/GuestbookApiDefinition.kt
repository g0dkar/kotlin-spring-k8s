package com.g0dkar.sample.client.api

import com.g0dkar.sample.client.model.GuestbookMessage
import com.g0dkar.sample.client.model.GuestbookMessageRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

/**
 * Describes the API provided by the service
 */
interface GuestbookApiDefinition {
    @GET("/guestbook")
    suspend fun getAll(
        @Query("offset") offset: Int = 0,
        @Query("max") max: Int = 50
    ): List<GuestbookMessage>

    @GET("/guestbook/{id}")
    suspend fun get(
        @Path("id") id: UUID
    ): GuestbookMessage

    @DELETE("/guestbook/{id}")
    suspend fun delete(
        @Path("id") id: UUID
    ): Any

    @POST("/guestbook")
    suspend fun create(
        @Body message: GuestbookMessageRequest
    ): GuestbookMessage
}
