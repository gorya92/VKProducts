package com.gorya.vk.ui.main

import android.util.Log
import com.gorya.vk.domain.entity.Product
import com.gorya.vk.domain.usecase.GetProductsUseCase
import com.gorya.vk.ui.main.MainContract.PartialStateChange.ProductsFirstPage
import com.gorya.vk.ui.main.MainContract.PartialStateChange.ProductNextPage
import com.gorya.vk.ui.main.MainContract.PartialStateChange.Refresh
import com.gorya.vk.ui.main.MainContract.ProductVS
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MainInteractorImpl @Inject constructor(
  private val getProductsUseCase: GetProductsUseCase,
) : MainContract.Interactor {
  init {
    Log.d("###", toString())
  }

  override fun productNextPageChanges(
    start: Int,
    limit: Int
  ): Flow<ProductNextPage> = flow { emit(getProductsUseCase(skip = start, limit = limit)) }
    .map { products ->
      products
        .map(MainContract::ProductVS)
        .let { ProductNextPage.Data(it) } as ProductNextPage
    }
    .onStart { emit(ProductNextPage.Loading) }
    .catch { emit(ProductNextPage.Error(it)) }

  override fun productFirstPageChanges(limit: Int): Flow<ProductsFirstPage> =
    flow { emit(getProductsUseCase(skip = 0, limit = limit)) }
      .map { products ->
        products.map(::ProductVS)
          .let { ProductsFirstPage.Data(it) } as ProductsFirstPage
      }
      .onStart { emit(ProductsFirstPage.Loading) }
      .catch { emit(ProductsFirstPage.Error(it)) }



  override fun refreshAll(
    limitProduct: Int
  ): Flow<Refresh> = flow {
    coroutineScope {
      val async2: Deferred<List<Product>> = async { getProductsUseCase(limit = limitProduct, skip = 0) }
      emit(
        Refresh.Success(
          products = async2.await().map(::ProductVS)
        ) as Refresh
      )
    }
  }.onStart { emit(Refresh.Refreshing) }
    .catch { emit(Refresh.Error(it)) }

}
