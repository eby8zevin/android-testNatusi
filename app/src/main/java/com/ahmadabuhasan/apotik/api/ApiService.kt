package com.ahmadabuhasan.apotik.api

import com.ahmadabuhasan.apotik.modal.ResponseDefault
import com.ahmadabuhasan.apotik.modal.ResponseDelete
import com.ahmadabuhasan.apotik.modal.ResponseGetObat
import com.ahmadabuhasan.apotik.modal.ResponseLogin
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseLogin>

    @FormUrlEncoded
    @POST("getObat")
    fun getObat(
        @Field("token") token: String
    ): Call<ResponseGetObat>

    @FormUrlEncoded
    @POST("createObat")
    fun createObat(
        @Field("token") token: String,
        @Field("id_obat") idObat: Int,
        @Field("nama_obat") nameObat: String,
        @Field("kategori_obat") categoryObat: String,
        @Field("stok_obat") stockObat: Int,
        @Field("harga_obat") priceObat: Int
    ): Call<ResponseDefault>

    @FormUrlEncoded
    @POST("deleteObat")
    fun deleteObat(
        @Field("token") token: String,
        @Field("id_obat") idObat: Int
    ): Call<ResponseDelete>
}