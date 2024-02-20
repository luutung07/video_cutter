package com.example.library_base.scroll

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class BaseRecyclerViewEndlessScrollListener(
    private var layoutManager: RecyclerView.LayoutManager
) : RecyclerView.OnScrollListener() {

    var lastPage: Boolean = false
    var isLoading = false

    abstract fun onLoadMore()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount = layoutManager.itemCount
        var pastVisibleItems = 0
        when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = (layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                pastVisibleItems = getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> pastVisibleItems = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            is LinearLayoutManager -> pastVisibleItems = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }

        if (!isLoading && totalItemCount > 0) {
            if (pastVisibleItems == totalItemCount - 1 && !lastPage) {
                onLoadMore()
                isLoading = true
            }
        }
    }

    fun resetState() {
        lastPage = false
    }

    fun setLoadMore(hasLoadMore: Boolean) {
        lastPage = !hasLoadMore
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0

        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }

        return maxSize
    }
}
