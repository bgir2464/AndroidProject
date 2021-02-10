package com.example.myapplication.auth.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.core.TAG
import com.example.myapplication.data.authRepo
import com.example.myapplication.model.LoginFormState
import com.example.myapplication.model.TokenHolder
import kotlinx.coroutines.launch
import com.example.myapplication.core.Result
class LoginViewModel : ViewModel() {
    private val mutableLoginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = mutableLoginFormState

    private val mutableLoginResult = MutableLiveData<Result<TokenHolder>>()
    val loginResult: LiveData<Result<TokenHolder>> = mutableLoginResult



    fun login(username: String, password: String) {
        viewModelScope.launch {
            Log.v(TAG, "login...");
            Log.v(TAG, username+' '+password);
            mutableLoginResult.value = authRepo.login(username, password)
//            Log.v(TAG,mutableLoginResult.value.token );
        }
    }
    fun logout() {
        viewModelScope.launch {

            mutableLoginResult.value =null
//            Log.v(TAG,mutableLoginResult.value.token );
        }
    }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            mutableLoginFormState.value = LoginFormState(usernameError =R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            mutableLoginFormState.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            mutableLoginFormState.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 1
    }
}