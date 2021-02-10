package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.core.Api
import com.example.myapplication.data.remote.RemoteAuthDataSource
import com.example.myapplication.model.TokenHolder
import com.example.myapplication.model.User
import com.example.myapplication.core.Result
import com.example.myapplication.core.TAG

object authRepo {

    var user: User? = null
        private set
    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        Api.tokenInterceptor.token = null
    }

    suspend fun login(username: String, password: String): Result<TokenHolder> {
        val user = User(username, password)
        Log.i(TAG, "authentication try.." )
        val result = RemoteAuthDataSource.login(user)
        if (result is Result.Success<TokenHolder>) {
            setLoggedInUser(user, result.data)
        }
        return result
    }

    private fun setLoggedInUser(user: User, tokenHolder: TokenHolder) {
        //se retine userul si tokenul

        Log.i(TAG, "the token is: " +tokenHolder.token)
        Log.i(TAG, "the user is: " +user.username)
        this.user = user
        Api.tokenInterceptor.token = tokenHolder.token
    }
}