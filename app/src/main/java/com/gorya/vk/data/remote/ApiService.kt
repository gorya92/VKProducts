package com.gorya.vk.data.remote

import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://dummyjson.com/"

interface ApiService {
  @GET("products")
  suspend fun getProducts(
    @Query("skip") skip: Int,
    @Query("limit") limit: Int
  ): Root

  companion object {
    operator fun invoke(retrofit: Retrofit) = retrofit.create<ApiService>()
  }
}
