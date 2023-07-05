package com.ahmadabuhasan.apotik.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ahmadabuhasan.apotik.api.ApiConfig
import com.ahmadabuhasan.apotik.databinding.ActivityLoginBinding
import com.ahmadabuhasan.apotik.modal.ResponseLogin
import com.ahmadabuhasan.apotik.utils.ConstVal.KEY_IS_LOGIN
import com.ahmadabuhasan.apotik.utils.ConstVal.KEY_TOKEN
import com.ahmadabuhasan.apotik.utils.SessionManager
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnLogin.setOnClickListener { checkLogin() }
    }

    private fun validateInputs(
        email: String,
        password: String
    ): Boolean {

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.error(this, "Invalid email", Toasty.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toasty.error(this, "Password cannot be empty", Toasty.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun checkLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (validateInputs(email, password)) {
            checkAPI(email, password)
        }
    }

    private fun checkAPI(email: String, password: String) {
        showLoading(true)
        val apiService = ApiConfig.getApiService().login(email, password)
        apiService.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                showLoading(false)
                if (response.isSuccessful) {

                    val responseBody = response.body()!!

                    when (val statusCode = response.code()) {
                        200 -> {
                            Toasty.success(
                                this@LoginActivity,
                                responseBody.message,
                                Toasty.LENGTH_SHORT, true
                            ).show()

                            sessionManager.apply {
                                setBooleanPref(KEY_IS_LOGIN, true)
                                setStringPref(KEY_TOKEN, responseBody.data.token)
                            }

                            val i = Intent(this@LoginActivity, MainActivity::class.java)
                            i.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                        }

                        500 -> {
                            Toasty.error(
                                this@LoginActivity,
                                responseBody.message,
                                Toasty.LENGTH_SHORT, true
                            ).show()
                        }

                        else -> {
                            Log.d("Response Code", "Other response code: $statusCode")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                showLoading(false)
                Log.e("Network Error", t.message.toString())
            }
        })
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        val isLogin = sessionManager.isLogin
        if (isLogin) {
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }
}