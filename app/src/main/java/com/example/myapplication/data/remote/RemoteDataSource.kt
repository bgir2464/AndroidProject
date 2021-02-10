package com.example.myapplication.data.remote
import com.example.myapplication.core.Api
import com.example.myapplication.core.Result
import com.example.myapplication.model.TokenHolder
import com.example.myapplication.model.User
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object RemoteAuthDataSource {
    //clasa care ia elementele de pe server
    interface AuthService {
        @Headers("Content-Type: application/json")
        @POST("/authenticate")
        suspend fun login(@Body user: User): TokenHolder
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<TokenHolder> {
        try {
            return Result.Success(authService.login(user))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}