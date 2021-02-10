package com.example.myapplication.data

import com.example.myapplication.core.Api
import com.example.myapplication.model.Medicament
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object MedicamentApi {
    private const val URL = "http://192.168.0.147:8080/"

    interface Service {
        @GET("/medicament/filter/{id}")
        suspend fun find(@Path("id") userid: String): List<Medicament>

        @GET("/medicament/{id}")
        suspend fun read(@Path("id") itemId: Int): Medicament;

        @Headers("Content-Type: application/json")
        @POST("/medicament")
        suspend fun create(@Body item: Medicament): Medicament

        @Headers("Content-Type: application/json")
        @PUT("/medicament/{id}")
        suspend fun update(@Path("id") itemId: Int, @Body item: Medicament): Medicament
    }
    val service: Service = Api.retrofit.create(Service::class.java)
}