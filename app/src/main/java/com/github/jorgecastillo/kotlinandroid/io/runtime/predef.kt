package com.github.jorgecastillo.kotlinandroid.io.runtime

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.reactivex.Observable

// itemCallback(getItemId::eqById)
// fun <A> ((A) -> Long).eqById(one: A, other: A): Boolean =
//      this(one) == this(other)

// itemCallback(eqById(getItemId))
fun <A> eqById(f : ((A) -> Long)) = { one: A, other: A ->
  f(one) == f(other)
}

fun <A> asyncListDelegationAdapter(
      getItemId: (A) -> Long,
      vararg delegates: AdapterDelegate<List<A>>
) = object : AsyncListDifferDelegationAdapter<A>(itemCallback(eqById(getItemId)), adapterDelegatesManager<List<A>>(*delegates)) {

        init {
          setHasStableIds(true)

        }

        override fun getItemId(position: Int): Long =
              getItemId(items[position])
      }

fun <A> adapterDelegatesManager(vararg delegates: AdapterDelegate<A>): AdapterDelegatesManager<A> =
      AdapterDelegatesManager<A>().apply {
        delegates.forEach {
          addDelegate(it)
        }
      }

fun <A> itemCallback(
      areItemsTheSame: (old: A, new: A) -> Boolean,
      areContentsTheSame: (oldItem: A, newItem: A) -> Boolean = { old, new -> old == new }
) = object : DiffUtil.ItemCallback<A>() {
  override fun areItemsTheSame(oldItem: A, newItem: A): Boolean =
        areItemsTheSame(oldItem, newItem)

  override fun areContentsTheSame(oldItem: A, newItem: A): Boolean =
        areContentsTheSame(oldItem, newItem)
}

fun attachOnLoadMoreListener(
      recyclerView: RecyclerView,
      loadingTriggerThreshold: Int = 3
): Observable<Int> = Observable.create<Int> { emitter ->
  val listener = object : RecyclerView.OnScrollListener() {

    var currentPage = 1
    var totalRequestedItems = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      super.onScrolled(recyclerView, dx, dy)
      val layoutManager = recyclerView.layoutManager ?: return

      val totalItemCount = layoutManager.itemCount

      val lastVisibleItemPosition = when (layoutManager) {
        is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
        // https://code.google.com/p/android/issues/detail?id=181461
        is StaggeredGridLayoutManager ->
          if (layoutManager.getChildCount() > 0) {
            layoutManager.findLastVisibleItemPositions(null)[0]
          } else {
            0
          }
        else -> throw IllegalStateException("LayoutManager needs to subclass LinearLayoutManager or StaggeredGridLayoutManager")
      }

      // Check if end of the list is reached (counting threshold)
      if (totalItemCount > 0 && totalItemCount <= (lastVisibleItemPosition + loadingTriggerThreshold)) {
        // Call load more only if we haven't requested all items for this page
        if (totalRequestedItems < totalItemCount) {
          totalRequestedItems = totalItemCount
          emitter.onNext(++currentPage)
        }
      }
    }
  }

  recyclerView.addOnScrollListener(listener)
  emitter.setCancellable { recyclerView.removeOnScrollListener(listener) }
}
      // Always do an initial emission since there won't be any scroll triggered at start
      .startWith(1)

val <T> T.exhaustive
  get() = this
