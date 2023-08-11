package com.ahmadabuhasan.apotik.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmadabuhasan.apotik.R
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

    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.etEmail.setText(getString(R.string.email_value))
        binding.etPassword.setText(getString(R.string.password_value))
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
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

                    if (responseBody.code == 200) {

                        val dataLogin = responseBody.data

                        sessionManager.apply {
                            setBooleanPref(KEY_IS_LOGIN, true)
                            setStringPref(KEY_TOKEN, dataLogin.token)
                        }

                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        i.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        Toasty.success(
                            this@LoginActivity,
                            responseBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toasty.error(
                            this@LoginActivity,
                            responseBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toasty.error(
                    this@LoginActivity,
                    "Akun Yang Anda Masukkan Tidak Terdaftar",
                    Toast.LENGTH_SHORT
                ).show()
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