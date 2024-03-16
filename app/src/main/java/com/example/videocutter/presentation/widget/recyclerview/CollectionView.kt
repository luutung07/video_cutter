package com.example.videocutter.presentation.widget.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Dimension
import androidx.annotation.IntRange
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseGridAdapter
import com.example.library_base.scroll.BaseRecyclerViewEndlessScrollListener
import com.example.videocutter.R
import java.util.Collections

class CollectionView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : RecyclerView(ctx, attrs) {

    companion object {

    }

    private var baseAdapter: BaseAdapter? = null
    private var layoutManagerMode: COLLECTION_MODE = COLLECTION_MODE.VERTICAL
    private var endlessScrollListener: BaseRecyclerViewEndlessScrollListener? = null
    private var loadMoreConsumer: (() -> Unit)? = null
    private var scrollConsumer: (() -> Unit)? = null
    private var emptyConsumer: (() -> Unit)? = null
    private var refreshConsumer: (() -> Unit)? = null
    private var hasFixedSize: Boolean = true
    private var isReverseView = false
    private var maxItemHorizontal: Int = 2
    private var maxItemGridHorizontal: Int = 2
    private var itemTouchHelper: TouchHelper? = null

    init {
        initView(attrs)
    }

    fun setAdapter(adapter: BaseAdapter) {
        baseAdapter = adapter
        this.adapter = baseAdapter
    }

    fun setAdapterCustom(adapter: BaseAdapter) {
        baseAdapter = adapter
        this.adapter = baseAdapter
    }

    fun setAdapter(adapter: BaseGridAdapter) {
        baseAdapter = adapter
        this.adapter = baseAdapter
    }

    fun isEmpty(): Boolean {
        return baseAdapter?.isEmpty() ?: true
    }

    fun setMaxItemHorizontal(@IntRange(from = 1, to = 10) number: Int) {
        maxItemHorizontal = number
        layoutManager = getGridLayoutManager()
    }

    fun setMaxItemHorizontal1(number: Int) {
        maxItemGridHorizontal = number
        layoutManager = getGridHorizontalLayoutManager()
    }

    fun setLayoutManager(
        mode: COLLECTION_MODE = COLLECTION_MODE.VERTICAL,
        isReverse: Boolean = false
    ) {
        layoutManagerMode = mode
        isReverseView = isReverse
        layoutManager = getLayoutManager(mode)
    }

    private fun getLayoutManager(mode: COLLECTION_MODE): LayoutManager {
        return when (mode) {
            COLLECTION_MODE.VERTICAL -> LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                isReverseView
            )

            COLLECTION_MODE.HORIZONTAL -> LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                isReverseView
            )

            COLLECTION_MODE.GRID_VERTICAL -> getGridLayoutManager()
            COLLECTION_MODE.GRID_HORIZONTAL -> getGridHorizontalLayoutManager()
        }
    }

    fun setCustomPadding(
        @Dimension left: Int = 0,
        @Dimension top: Int = 0,
        @Dimension bottom: Int = 0,
        @Dimension right: Int = 0
    ) {
        updatePadding(left, top, right, bottom)
    }

    fun setCloseRefreshListener(consumer: (() -> Unit)? = null) {
        refreshConsumer = consumer
    }

    fun setLoadMoreListener(consumer: (() -> Unit)? = null) {
        loadMoreConsumer = consumer

        endlessScrollListener =
            object : BaseRecyclerViewEndlessScrollListener(this.layoutManager!!) {
                override fun onLoadMore() {
                    baseAdapter?.makeLoadMore()
                    baseAdapter?.itemCount?.let { smoothScrollToPosition(it) }
                    loadMoreConsumer?.invoke()
                }
            }

        endlessScrollListener?.let {
            this.addOnScrollListener(it)
        }
    }

    fun setScrollListener(consumer: (() -> Unit)? = null) {
        scrollConsumer = consumer
    }

    fun setEmptyListener(consumer: (() -> Unit)? = null) {
        emptyConsumer = consumer
    }

    fun showLoading() {
        hideLoading()
        baseAdapter?.makeLoading()
    }

    fun hideLoading() {
        baseAdapter?.removeLoading()
    }

    fun submitList(newData: List<Any>? = null, hasLoadMore: Boolean = true) {
        hideLoading()
        if (baseAdapter != null && baseAdapter!!.dataList.isNotEmpty()) {
            baseAdapter?.removeLoadMore()
        }
        endlessScrollListener?.setLoadMore(hasLoadMore)
        baseAdapter?.submitList(newData)
        if (newData != null) {
            if (newData.isEmpty()) {
                baseAdapter?.makeEmpty()
            }
        } else {
            baseAdapter?.makeLoading()
        }
        endlessScrollListener?.isLoading = false
        refreshConsumer?.invoke()
    }

    fun submitEmptyList() {
        submitList(emptyList(), false)
    }

    fun removeEmpty() {
        baseAdapter?.removeEmpty()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun blockScroll(isBlock: Boolean) {
        requestDisallowInterceptTouchEvent(isBlock)
    }

    fun setDragRecyclerView(
        dragLastItem: Boolean = true,
        swap: ((Int, Int) -> Unit)
    ) {
        itemTouchHelper = object : TouchHelper() {
            override val dataList: MutableList<Any>?
                get() = baseAdapter?.dataList
            override val isDragItemLast: Boolean
                get() = dragLastItem

            override fun eventMove(oldIndex: Int, newIndex: Int) {
                if (dataList != null) {
                    Collections.swap(dataList!!, oldIndex, newIndex)
                    baseAdapter?.notifyItemMoved(oldIndex, newIndex)
                    swap.invoke(oldIndex, newIndex)
                }
            }

        }
        itemTouchHelper?.let {
            ItemTouchHelper(it).attachToRecyclerView(this)
        }
    }

    private fun getGridLayoutManager(): LayoutManager {
        val spanCount = getOptimalSpanCount(maxItemHorizontal)
        val gridLayoutManager =
            GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (baseAdapter is BaseGridAdapter) {
                    (baseAdapter as BaseGridAdapter).getItemSpanSize(position, spanCount)
                } else {
                    spanCount
                }
            }
        }

        return gridLayoutManager
    }

    private fun getGridHorizontalLayoutManager(): LayoutManager {
        return GridLayoutManager(
            context,
            maxItemGridHorizontal,
            GridLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun getOptimalSpanCount(maxItemHorizontal: Int): Int {
        var spanCount = 1
        if (maxItemHorizontal <= 0) {
            return 0
        }
        for (i in 1..maxItemHorizontal) {
            spanCount *= maxItemHorizontal
        }
        return spanCount
    }

    private fun initView(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CollectionView, 0, 0)

        this.adapter = baseAdapter
        overScrollMode = OVER_SCROLL_NEVER
        setLayoutManager()
        this.apply {
            setHasFixedSize(hasFixedSize)
            itemAnimator = DefaultItemAnimator()
            setItemViewCacheSize(50)
        }

        ta.recycle()
    }
}
