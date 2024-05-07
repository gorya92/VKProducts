package com.gorya.vk.data.remote

import com.squareup.moshi.Json


data class Root(
  @Json(name = "products")
  val products: List<ProductResponse>,
  @Json(name = "total")
  val total: Long,
  @Json(name = "skip")
  val skip: Long,
  @Json(name = "limit")
  val limit: Long,
)

data class ProductResponse(
  @Json(name = "id")
  val id: Long,
  @Json(name = "title")
  val title: String,
  @Json(name = "description")
  val description: String,
  @Json(name = "price")
  val price: Long,
  @Json(name = "discountPercentage")
  val discountPercentage: Double,
  @Json(name = "rating")
  val rating: Double,
  @Json(name = "stock")
  val stock: Long,
  @Json(name = "brand")
  val brand: String,
  @Json(name = "category")
  val category: String,
  @Json(name = "thumbnail")
  val thumbnail: String,
  @Json(name = "images")
  val images: List<String>,
)
