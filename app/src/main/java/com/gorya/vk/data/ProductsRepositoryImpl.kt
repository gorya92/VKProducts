package com.gorya.vk.data

import com.gorya.vk.data.remote.ApiService
import com.gorya.vk.domain.dispatchers_schedulers.CoroutinesDispatchersProvider
import com.gorya.vk.domain.entity.Product
import com.gorya.vk.domain.repository.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.withContext

class ProductsRepositoryImpl@Inject constructor(
  private val apiService: ApiService,
  private val dispatchersProvider: CoroutinesDispatchersProvider
) : ProductRepository {
  override suspend fun getProducts(skip: Int, limit: Int): List<Product> {
    return withContext(dispatchersProvider.io) {
      apiService.getProducts(skip = skip, limit = limit).products.map {
        Product(
          id = it.id,
          title = it.title,
          description = it.description,
          thumbnail = it.thumbnail,
          images = it.images,
          price =  it.price,
          discountPercentage = it.discountPercentage,
          rating = it.rating,
          stock = it.stock,
          brand = it.brand,
          category = it.category
        )
      }
    }
  }
}
