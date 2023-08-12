package com.ahmadabuhasan.apotik.modal

import com.google.gson.annotations.SerializedName

data class ResponseDelete(
    @SerializedName("status")
    val status: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String
)
