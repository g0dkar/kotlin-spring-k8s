package com.g0dkar.sample.client.api

import com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.UUID

interface GuestbookApi {
    @GET("/guestbook")
    fun getAll(
        @Query("offset") offset: Int = 0,
        @Query("max") max: Int = 50
    ): Call<List<GuestbookMessageResponse>>

    @GET("/guestbook/{id}")
    fun getMessage(
        @Query("id") id: UUID
    ): Call<GuestbookMessageResponse>
}