package com.gorya.vk.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gorya.vk.R
import com.gorya.vk.databinding.RecyclerItemProductBinding
import com.gorya.vk.ui.main.MainContract.Item
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

private object DiffUtilItemCallback : DiffUtil.ItemCallback<Item>() {
  override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
    return when {
      oldItem is Item.Product && newItem is Item.Product -> oldItem.product.id == newItem.product.id
      else -> oldItem == newItem
    }
  }

  override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem

  override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
    return when {
      oldItem is Item.Product && newItem is Item.Product -> newItem.product
      else -> null
    }
  }
}

class MainAdapter(
) :
  ListAdapter<Item, MainAdapter.VH>(DiffUtilItemCallback) {
  private val scrollToFirstSF = MutableSharedFlow<Unit>()

  private val retrySF = MutableSharedFlow<Unit>()
  val retryFlow: SharedFlow<Unit> get() = retrySF.asSharedFlow()



  override fun onCreateViewHolder(parent: ViewGroup, @LayoutRes viewType: Int): VH {
    val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
    return when (viewType) {
      R.layout.recycler_item_product -> ProductVH(RecyclerItemProductBinding.bind(itemView))
      else -> error("Unknown viewType=$viewType")
    }
  }

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

  override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
    if (payloads.isEmpty()) return holder.bind(getItem(position))
    Log.d("###", "[PAYLOAD] MAIN size=${payloads.size}")
    payloads.forEach { payload ->
      Log.d("###", "[PAYLOAD] $payload")
      when {
        payload is Item.Product && holder is ProductVH -> holder.update( payload)
      }
    }
  }

  @LayoutRes
  override fun getItemViewType(position: Int) = getItem(position).viewType


  abstract class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Item)
  }

  private class ProductVH(private val binding: RecyclerItemProductBinding) : VH(binding.root) {
    override fun bind(item: Item) {
      if (item !is Item.Product) return
      update(item)
    }

    fun update(item: Item.Product) = binding.run {
      title.text =  item.product.title
      subtitle.text = item.product.description
      price.text = String.format(RUBLES, item.product.price)
      stock.text = String.format(REMAINING, item.product.stock)
      discountPercentage.text = if (item.product.discountPercentage != 0.toDouble()) {
        String.format(PERCENT, item.product.discountPercentage)
      } else {
        ""
      }
      fullPrice.text = item.getFullPrice()
      Glide.with(image)
        .load(item.product.thumbnail)
        .error(R.drawable.placeholder)
        .apply(RequestOptions.bitmapTransform( RoundedCorners(80)))
        .into(image)

    }
  }


  override fun onViewRecycled(holder: VH) {
    super.onViewRecycled(holder)
  }

  suspend fun scrollHorizontalListToFirst() = scrollToFirstSF.emit(Unit)

  companion object{
    private const val REMAINING = "Осталось %d шт"
    private const val PERCENT = "-%.2f%%"
    private const val RUBLES = "%d₽"

  }
}
