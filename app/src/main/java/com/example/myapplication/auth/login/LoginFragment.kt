package com.example.myapplication.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_login.*
import com.example.myapplication.core.Result
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(

            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupLoginForm()
    }

    override fun onResume() {
        super.onResume()
        viewModel.logout()
    }
    private fun setupLoginForm(){
        viewModel.loginFormState.observe(viewLifecycleOwner, { loginState ->
            login.isEnabled = loginState.isDataValid
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null){
                password.error = getString(loginState.passwordError)
            }
        })
        viewModel.loginResult.observe(viewLifecycleOwner, { loginResult ->
            loading.visibility = View.GONE
            if (loginResult is Result.Success<*>) {
                val args = Bundle()

                args.putString("USER_ID", username.text.toString())
                args.putBoolean("FR_LOGIN", true)
                findNavController().navigate(R.id.medicamentFragment,args)
            } else if (loginResult is Result.Error) {
                error_text.text = "Login error ${loginResult.exception.message}"
                error_text.visibility = View.VISIBLE
            }
        })
        username.afterTextChanged {
            viewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }
        password.afterTextChanged {
            viewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }
        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            error_text.visibility = View.GONE
            viewModel.login(username.text.toString(), password.text.toString())
        }
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}