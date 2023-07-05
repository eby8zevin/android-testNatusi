package com.ahmadabuhasan.apotik.modal

import com.google.gson.annotations.SerializedName

data class ResponseGetObat(

    @SerializedName("status")
    val status: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val dataObat: ArrayList<ListObat>
)

data class ListObat(

    @SerializedName("id_obat")
    val idObat: Int,

    @SerializedName("nama_obat")
    val nameObat: String,

    @SerializedName("kategori_obat")
    val categoryObat: String,

    @SerializedName("stok_obat")
    val stockObat: Int,

    @SerializedName("harga_obat")
    val priceObat: Int,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null

)
