package com.ahmadabuhasan.apotik.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.apotik.adapter.AdapterObat
import com.ahmadabuhasan.apotik.api.ApiConfig
import com.ahmadabuhasan.apotik.databinding.ActivityMainBinding
import com.ahmadabuhasan.apotik.modal.ListObat
import com.ahmadabuhasan.apotik.modal.ResponseGetObat
import com.ahmadabuhasan.apotik.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: AdapterObat

    private var token: String? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        token = sessionManager.getToken

        binding.rvObat.layoutManager = LinearLayoutManager(this)
        binding.rvObat.setHasFixedSize(true)
        adapter = AdapterObat(this, arrayListOf(), token.toString())
        adapter.notifyDataSetChanged()
        binding.rvObat.adapter = adapter
        getObat(token.toString())

        binding.fabAdd.setOnClickListener { toCreateData() }
    }

    private fun getObat(token: String) {
        val apiService = ApiConfig.getApiService().getObat(token)
        apiService.enqueue(object : Callback<ResponseGetObat> {
            override fun onResponse(
                call: Call<ResponseGetObat>,
                response: Response<ResponseGetObat>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setData(responseBody.dataObat)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetObat>, t: Throwable) {
                Log.e("Network Error", t.message.toString())
            }
        })
    }

    fun setData(data: ArrayList<ListObat>) {
        adapter.setData(data)
    }

    private fun toCreateData() {
        val i = Intent(this, CreateActivity::class.java)
        startActivity(i)
    }
}