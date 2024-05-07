package com.gorya.vk.domain.entity

import com.squareup.moshi.Json

data class Product (
  val id: Long,
  val title: String,
  val description: String,
  val thumbnail: String,
  val images: List<String>,
  val price: Long,
  val discountPercentage: Double,
  val rating: Double,
  val stock: Long,
  val brand: String,
  val category: String,
)
