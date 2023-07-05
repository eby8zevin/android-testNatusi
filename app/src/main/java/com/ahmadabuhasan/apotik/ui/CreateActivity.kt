package com.ahmadabuhasan.apotik.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ahmadabuhasan.apotik.api.ApiConfig
import com.ahmadabuhasan.apotik.databinding.ActivityCreateBinding
import com.ahmadabuhasan.apotik.modal.ResponseDefault
import com.ahmadabuhasan.apotik.utils.SessionManager
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding
    private lateinit var sessionManager: SessionManager

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        token = sessionManager.getToken

        binding.btnSave.setOnClickListener { checkData() }
    }

    private fun checkData() {
        val idObat = binding.etId.text.toString()
        val name = binding.etName.text.toString()
        val category = binding.etCategory.text.toString()
        val stock = binding.etStock.text.toString()
        val price = binding.etPrice.text.toString()

        if (name.isEmpty() && category.isEmpty() && stock.isEmpty() && price.isEmpty()) {
            Toasty.info(this, "Cannot be empty", Toasty.LENGTH_SHORT, true).show()
        } else {
            insertData(idObat.toInt(), name, category, stock.toInt(), price.toInt())
        }
    }

    private fun insertData(id: Int, name: String, category: String, stock: Int, price: Int) {
        val apiService =
            ApiConfig.getApiService().createObat(token.toString(), id, name, category, stock, price)
        apiService.enqueue(object : Callback<ResponseDefault> {
            override fun onResponse(
                call: Call<ResponseDefault>,
                response: Response<ResponseDefault>
            ) {
                if (response.isSuccessful) {
                    val statusCode = response.code()

                    if (statusCode == 200) {
                        Toasty.success(
                            this@CreateActivity,
                            response.body()!!.message,
                            Toasty.LENGTH_SHORT
                        ).show()

                        val i = Intent(this@CreateActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        Toasty.error(
                            this@CreateActivity,
                            response.body()!!.message,
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                Log.e("Network Error", t.message.toString())
            }
        })
    }
}