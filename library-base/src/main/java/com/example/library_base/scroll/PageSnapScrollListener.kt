package com.example.library_base.scroll

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library_base.scroll.BaseRecyclerViewEndlessScrollListener

abstract class PageSnapScrollListener(layoutManager: RecyclerView.LayoutManager) : BaseRecyclerViewEndlessScrollListener(layoutManager) {
    private var firstVisibleItem = 0
    private var visibleItemCount = 0

    @Volatile
    private var enabled = false
    private var preLoadCount = 0

    abstract fun onItemIsFirstVisibleItem(index: Int)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (enabled) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager
            if (manager is LinearLayoutManager) {
                visibleItemCount = manager.childCount
                firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItem != -1) {
                    Log.d("anhnd", "firstVisibleItem: $firstVisibleItem")
                }
                onItemIsFirstVisibleItem(firstVisibleItem)
            } else {
                throw RuntimeException("Expected recyclerview to have linear layout manager")
            }
        }
    }

    fun disableScrollListener() {
        enabled = false
    }

    fun enableScrollListener() {
        enabled = true
    }

    fun setPreLoadCount(preLoadCount: Int) {
        this.preLoadCount = preLoadCount
    }
}
