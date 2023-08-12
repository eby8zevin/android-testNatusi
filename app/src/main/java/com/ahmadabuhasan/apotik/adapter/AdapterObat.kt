package com.ahmadabuhasan.apotik.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.apotik.R
import com.ahmadabuhasan.apotik.api.ApiConfig
import com.ahmadabuhasan.apotik.databinding.ItemObatBinding
import com.ahmadabuhasan.apotik.modal.ListObat
import com.ahmadabuhasan.apotik.modal.ResponseDelete
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

                ivDelete.setOnClickListener { deleteData(obat.idObat, adapterPosition) }
            }

            itemView.setOnClickListener {
                Toasty.info(context, "Maintenance", Toasty.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = obatList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<ListObat>) {
        obatList.clear()
        obatList.addAll(data)
        notifyDataSetChanged()
    }

    private fun deleteData(id: Int, position: Int) {
        AlertDialog.Builder(context)
            .setMessage(R.string.want_to_delete)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialogInterface, _ ->
                val apiService = ApiConfig.getApiService().deleteObat(token, id)
                apiService.enqueue(object : Callback<ResponseDelete> {
                    override fun onResponse(
                        call: Call<ResponseDelete>,
                        response: Response<ResponseDelete>
                    ) {
                        val responseBody = response.body()
                        if (response.isSuccessful && responseBody != null) {
                            Toasty.success(
                                context,
                                responseBody.message,
                                Toasty.LENGTH_SHORT,
                                true
                            ).show()
                            obatList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }

                    override fun onFailure(call: Call<ResponseDelete>, t: Throwable) {
                        dialogInterface.cancel()
                        Log.e("Network Error", t.message.toString())
                    }
                })
            }
            .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }
}
