package com.ahmadabuhasan.apotik.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.apotik.R
import com.ahmadabuhasan.apotik.api.ApiConfig
import com.ahmadabuhasan.apotik.databinding.ItemObatBinding
import com.ahmadabuhasan.apotik.modal.ListObat
import com.ahmadabuhasan.apotik.modal.ResponseDefault
import com.ahmadabuhasan.apotik.ui.MainActivity
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterObat(
    private val context: Context,
    private val obatList: ArrayList<ListObat>,
    private val token: String
) : RecyclerView.Adapter<AdapterObat.ObatViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterObat.ObatViewHolder {
        val binding = ItemObatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ObatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterObat.ObatViewHolder, position: Int) {
        obatList[position].let { obat ->
            holder.bind(obat)
        }
    }

    override fun getItemCount(): Int = obatList.size

    inner class ObatViewHolder(private val binding: ItemObatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(obat: ListObat) {
            with(binding) {

                tvName.text = obat.nameObat
                tvCategory.text = buildString {
                    append("Category: ")
                    append(obat.categoryObat)
                }
                tvStock.text = buildString {
                    append("Stock: ")
                    append(obat.stockObat)
                }
                tvPrice.text = buildString {
                    append("Price: ")
                    append(obat.priceObat)
                }

                ivDelete.setOnClickListener { deleteData(obat.idObat) }
            }

            itemView.setOnClickListener {
                Toasty.info(context, "Maintenance", Toasty.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<ListObat>) {
        obatList.clear()
        obatList.addAll(data)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        obatList.clear()
        notifyDataSetChanged()
    }

    private fun deleteData(id: Int) {
        AlertDialog.Builder(context)
            .setMessage(R.string.want_to_delete)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialogInterface, _ ->
                val apiService = ApiConfig.getApiService().deleteObat(token, id)
                apiService.enqueue(object : Callback<ResponseDefault> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<ResponseDefault>,
                        response: Response<ResponseDefault>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()!!

                            Toasty.success(
                                context,
                                responseBody.message,
                                Toasty.LENGTH_SHORT,
                                true
                            ).show()

                            obatList.remove(responseBody.data)
                            notifyItemRemoved(id)
                            clearData()

                            context as Activity
                            this@AdapterObat.context.finish()
                            this@AdapterObat.context.startActivity(context.intent)
                            val i = Intent(context, MainActivity::class.java)
                            context.startActivity(i)
                            context.finish()
                        }
                    }

                    override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                        dialogInterface.cancel()
                        Log.e("Network Error", t.message.toString())
                    }
                })
            }
            .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }
}
