package com.gorya.vk.ui.main

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import androidx.annotation.LayoutRes
import com.gorya.vk.R
import com.gorya.vk.domain.entity.Product
import kotlinx.coroutines.flow.Flow


interface MainContract {
  data class ViewState(
    val items: List<Item>, val productItems: List<Item.Product>, val isRefreshing: Boolean
  ) {


    companion object Factory {
      @JvmStatic
      fun initial() = ViewState(
        items = listOf(

        ), productItems = emptyList(), isRefreshing = false
      )
    }
  }

  sealed class Item(@LayoutRes val viewType: Int) {


    data class Product(val product: ProductVS) : Item(R.layout.recycler_item_product) {
      fun getFullPrice(): SpannableString {
        if (product.discountPercentage != 0.toDouble()) {
          val discount = product.price * product.discountPercentage / 100
          val fullPriceStr = "${(product.price - discount).toInt()} ₽"
          val spannableString = SpannableString(fullPriceStr)


          spannableString.setSpan(
            StrikethroughSpan(), 0, fullPriceStr.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
          )

          spannableString.setSpan(
            ForegroundColorSpan(Color.GRAY),
            0,
            fullPriceStr.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
          )
          return spannableString
        } else {
          return SpannableString("")
        }
      }
    }

  }

  data class ProductVS(
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
  ) {
    constructor(domain: Product) : this(
      id = domain.id,
      title = domain.title,
      description = domain.description,
      thumbnail = domain.thumbnail,
      images = domain.images,
      price = domain.price,
      discountPercentage = domain.discountPercentage,
      rating = domain.rating,
      stock = domain.stock,
      brand = domain.brand,
      category = domain.category
    )
  }

  sealed class PlaceholderState {
    object Loading : PlaceholderState()
    object Idle : PlaceholderState()
    data class Error(val error: Throwable) : PlaceholderState()

    override fun toString() = when (this) {
      Loading -> "PlaceholderState::Loading"
      Idle -> "PlaceholderState::Idle"
      is Error -> "PlaceholderState::Error($error)"
    }
  }

  sealed class ViewIntent {
    object Initial : ViewIntent()
    object Refresh : ViewIntent()

    // Vertical
    object LoadNextPage : ViewIntent()

    object RetryLoadPage : ViewIntent()

    // Horizontal
    object LoadNextPageHorizontal : ViewIntent()

    object RetryLoadPageHorizontal : ViewIntent()

    object RetryHorizontal : ViewIntent()
  }

  sealed class PartialStateChange {
    abstract fun reduce(vs: ViewState): ViewState

    sealed class ProductsFirstPage : PartialStateChange() {
      data class Data(val products: List<ProductVS>) : ProductsFirstPage()
      data class Error(val error: Throwable) : ProductsFirstPage()
      object Loading : ProductsFirstPage()

      override fun reduce(vs: ViewState): ViewState {
        return when (this) {
          is Data -> {
            val productItems = this.products.map { Item.Product(it) }
            vs.copy(items = vs.items.filter { it !is Item.Product } + productItems,
              productItems = productItems)
          }

          is Error -> vs.copy(
            items = vs.items.filter { it !is Item.Product }, productItems = emptyList()
          )

          Loading -> vs.copy(items = vs.items.filter { it !is Item.Product })
        }
      }
    }

    sealed class ProductNextPage : PartialStateChange() {
      data class Data(val products: List<ProductVS>) : ProductNextPage()
      data class Error(val error: Throwable) : ProductNextPage()
      object Loading : ProductNextPage()

      override fun reduce(vs: ViewState): ViewState {
        return when (this) {
          is Data -> {
            val productItems =
              vs.items.filterIsInstance<Item.Product>() + this.products.map { Item.Product(it) }

            vs.copy(items = vs.items.filter { it !is Item.Product } + productItems,
              productItems = productItems)
          }

          is Error -> vs.copy(
          )

          Loading -> vs.copy(
          )
        }
      }
    }


    sealed class Refresh : PartialStateChange() {
      data class Success(val products: List<ProductVS>) : Refresh()
      data class Error(val error: Throwable) : Refresh()
      object Refreshing : Refresh()

      override fun reduce(vs: ViewState): ViewState {
        return when (this) {
          is Success -> {
            listOf(
              ProductsFirstPage.Data(products),
            ).fold(vs.copy(isRefreshing = false)) { acc, change -> change.reduce(acc) }
          }

          is Error -> vs.copy(isRefreshing = false)
          Refreshing -> vs.copy(isRefreshing = true)
        }
      }
    }

    fun toEvent(): SingleEvent? = when (this) {
      is ProductsFirstPage.Data -> if (products.isEmpty()) SingleEvent.HasReachedMax else null
      is ProductsFirstPage.Error -> SingleEvent.GetProductsFailure(error)
      ProductsFirstPage.Loading -> null

      is ProductNextPage.Data -> if (products.isEmpty()) SingleEvent.HasReachedMax else null
      is ProductNextPage.Error -> SingleEvent.GetProductsFailure(error)
      ProductNextPage.Loading -> null

      is Refresh.Success -> SingleEvent.RefreshSuccess
      is Refresh.Error -> SingleEvent.RefreshFailure(error)
      Refresh.Refreshing -> null

    }
  }

  sealed class SingleEvent {
    object RefreshSuccess : SingleEvent()
    data class RefreshFailure(val error: Throwable) : SingleEvent()

    data class GetProductsFailure(val error: Throwable) : SingleEvent()

    object HasReachedMax : SingleEvent()
  }

  interface Interactor {
    fun productNextPageChanges(start: Int, limit: Int): Flow<PartialStateChange.ProductNextPage>
    fun productFirstPageChanges(limit: Int): Flow<PartialStateChange.ProductsFirstPage>

    fun refreshAll(limitProduct: Int): Flow<PartialStateChange.Refresh>
  }
}
