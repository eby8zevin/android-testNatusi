package com.ahmadabuhasan.apotik.modal

import com.google.gson.annotations.SerializedName

data class ResponseDefault(

    @SerializedName("status")
    val status: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ListObat? = null
)
