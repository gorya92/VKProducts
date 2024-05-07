package com.gorya.vk.domain.usecase

import com.gorya.vk.domain.entity.Product
import com.gorya.vk.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
  private val postRepository: ProductRepository
) {
  suspend operator fun invoke(skip: Int, limit: Int): List<Product> {
    return postRepository.getProducts(skip = skip, limit = limit)
  }
}
