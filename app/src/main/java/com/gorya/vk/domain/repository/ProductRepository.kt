package com.gorya.vk.domain.repository

import com.gorya.vk.domain.entity.Product


interface ProductRepository {
    suspend fun getProducts(
      skip: Int,
      limit: Int
    ): List<Product>

}
