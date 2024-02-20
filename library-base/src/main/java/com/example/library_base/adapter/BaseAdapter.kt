package com.example.library_base.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.library_base.extension.STRING_DEFAULT
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDrawable
import com.example.library_base.R
import com.example.library_base.databinding.BaseEmptyDefaultItemBinding
import com.example.library_base.databinding.BaseLoadMoreDefaultItemBinding
import com.example.library_base.databinding.BaseLoadingShimmerItemBinding

abstract class BaseAdapter : RecyclerView.Adapter<BaseVH<Any>>() {
    companion object {
        const val INVALID_RESOURCE = -1
        const val LOADING_VIEW_TYPE = 100
        const val LOAD_MORE_VIEW_TYPE = 101
        const val EMPTY_VIEW_TYPE = 102
        const val NORMAL_VIEW_TYPE = 103
    }

    var dataList: MutableList<Any> = mutableListOf()
        private set

    private lateinit var myInflater: LayoutInflater

    abstract fun getLayoutResource(viewType: Int): Int

    abstract fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>?

    fun getDataAtPosition(position: Int): Any {
        return dataList[position]
    }

    open fun setupLoadMoreState() = LoadMore()

    open fun setupEmptyState() = Empty()

    private fun getLayoutLoadMore() = R.layout.base_load_more_default_item

    open fun getLayoutLoading() = R.layout.base_loading_default_item

    private fun getLayoutEmpty() = R.layout.base_empty_default_item

    open fun getItemViewTypeCustom(position: Int): Int = NORMAL_VIEW_TYPE

    open fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return BaseDiffUtilCallback(oldList, newList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseVH<Any> {
        if (!::myInflater.isInitialized) {
            myInflater = LayoutInflater.from(parent.context)
        }
        val binding: ViewDataBinding
        return when (viewType) {
            LOADING_VIEW_TYPE -> {
                binding = DataBindingUtil.inflate(myInflater, getLayoutLoading(), parent, false)
                LoadingVH(binding) as BaseVH<Any>

            }

            LOAD_MORE_VIEW_TYPE -> {
                binding = if (setupLoadMoreState().overrideLayoutRes == null) {
                    DataBindingUtil.inflate(myInflater, getLayoutLoadMore(), parent, false)
                } else {
                    DataBindingUtil.inflate(
                        myInflater,
                        setupLoadMoreState().overrideLayoutRes!!,
                        parent,
                        false
                    )
                }
                LoadMoreVH(binding) as BaseVH<Any>
            }

            EMPTY_VIEW_TYPE -> {
                binding = if (setupEmptyState().overrideLayoutRes == null) {
                    DataBindingUtil.inflate(myInflater, getLayoutEmpty(), parent, false)
                } else {
                    DataBindingUtil.inflate(
                        myInflater,
                        setupEmptyState().overrideLayoutRes!!,
                        parent,
                        false
                    )
                }
                EmptyVH(binding) as BaseVH<Any>
            }

            else -> {
                if (getLayoutResource(viewType) != INVALID_RESOURCE) {
                    binding = DataBindingUtil.inflate(
                        myInflater,
                        getLayoutResource(viewType),
                        parent,
                        false
                    )
                    onCreateViewHolder(viewType, binding) as BaseVH<Any>
                } else {
                    throw IllegalArgumentException("Can not find layout for type: $viewType")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @Deprecated("không override func này, muốn dùng view type thì override func #getItemViewTypeCustom(position: Int)")
    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is Loading -> LOADING_VIEW_TYPE
            is LoadMore -> LOAD_MORE_VIEW_TYPE
            is Empty -> EMPTY_VIEW_TYPE
            else -> getItemViewTypeCustom(position)
        }
    }

    override fun onBindViewHolder(holder: BaseVH<Any>, position: Int) {
        holder.onBind(getDataAtPosition(position))
    }

    override fun onBindViewHolder(
        holder: BaseVH<Any>,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBind(getDataAtPosition(position), payloads)
        }
    }

    fun submitList(newData: List<Any>?) {
        val newList = newData?.toMutableList()
        if (newList != null) {
            val callback = getDiffUtil(dataList, newList)
            val diffResult = DiffUtil.calculateDiff(callback)
            this.dataList = newList
            diffResult.dispatchUpdatesTo(this)
        }
    }

    @Deprecated("không sử dụng nữa, chuyển sang dùng #submitList")
    @SuppressLint("NotifyDataSetChanged")
    open fun resetData(dataList: List<Any>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    @Deprecated("không sử dụng nữa, chuyển sang dùng #submitList")
    fun addListItem(dataList: List<Any>) {
        val size = this.dataList.size
        this.dataList.addAll(dataList)
        notifyItemRangeInserted(size, dataList.size)
    }

    @Deprecated("không sử dụng nữa, chuyển sang dùng #submitList")
    fun add(item: Any) {
        this.dataList.add(item)
        notifyItemInserted(dataList.size)
    }

    @Deprecated("không sử dụng nữa, chuyển sang dùng #submitList")
    fun addFirstPosition(item: Any) {
        this.dataList.add(0, item)
        notifyItemInserted(0)
    }

    @Deprecated("không sử dụng nữa, chuyển sang dùng #submitList")
    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        if (dataList.isNotEmpty()) {
            dataList.clear()
            notifyDataSetChanged()
        }
    }

    fun removeLastItem() {
        if (dataList.isNotEmpty()) {
            val lastIndex = dataList.lastIndex
            if (lastIndex >= 0) {
                dataList.removeLast()
                notifyItemRemoved(lastIndex)
            }
        }
    }

    fun makeLoadMore() {
        add(setupLoadMoreState())
    }

    fun removeLoadMore() {
        if (dataList.isNotEmpty()) {
            val lastIndex = dataList.lastIndex
            if (lastIndex >= 0) {
                if (dataList[lastIndex] is LoadMore) {
                    dataList.removeLast()
                    notifyItemRemoved(lastIndex)
                }
            }
        }
    }

    fun makeLoadMoreReverse() {
        addFirstPosition(setupLoadMoreState())
    }

    fun removeLoadMoreReverse() {
        if (dataList.isNotEmpty()) {
            if (dataList[0] is LoadMore) {
                dataList.removeAt(0)
                notifyItemRemoved(0)
            }
        }
    }

    fun makeLoading() {
        add(Loading())
    }

    fun removeLoading() {
        if (dataList.isNotEmpty()) {
            val loadingItem = dataList.find {
                it is Loading
            }
            val loadingIndex = dataList.indexOfFirst {
                it == loadingItem
            }
            if (loadingIndex >= 0) {
                dataList.remove(loadingItem)
                notifyItemRemoved(loadingIndex)
            }
        }
    }

    fun makeEmpty() {
        add(setupEmptyState())
    }

    fun removeEmpty() {
        if (dataList.isNotEmpty()) {
            if (dataList.isNotEmpty()) {
                val lastIndex = dataList.lastIndex
                if (lastIndex >= 0) {
                    if (dataList[lastIndex] is Empty) {
                        dataList.removeLast()
                        notifyItemRemoved(lastIndex)
                    }
                }
            }
        }
    }

    fun isEmpty(): Boolean {
        return dataList.isEmpty()
    }

    inner class LoadingVH(binding: ViewDataBinding) : BaseVH<Loading>(binding) {
        private val viewDataBinding = binding as? BaseLoadingShimmerItemBinding

        override fun onBind(data: Loading) {
            super.onBind(data)
            if (viewDataBinding != null) {
                if (dataList.isEmpty()) {
                    viewDataBinding.sflShimmerBase.startShimmer()
                } else {
                    viewDataBinding.sflShimmerBase.stopShimmer()
                }
            }
        }
    }

    inner class LoadMoreVH(binding: ViewDataBinding) : BaseVH<LoadMore>(binding) {
        private val viewDataBinding = binding as? BaseLoadMoreDefaultItemBinding

        override fun onBind(data: LoadMore) {
            super.onBind(data)
            if (viewDataBinding != null) {
                viewDataBinding.tvBaseLoadMoreDefaultItmMessage.apply {
                    text = data.message
                    setTextColor(data.messageColor)
                }
                viewDataBinding.pbBaseLoadMoreDefaultItmLoading.indeterminateDrawable.setColorFilter(
                    data.progressBarColor,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    inner class EmptyVH(binding: ViewDataBinding) : BaseVH<Empty>(binding) {
        private val viewDataBinding = binding as? BaseEmptyDefaultItemBinding

        override fun onBind(data: Empty) {
            super.onBind(data)
            if (viewDataBinding != null) {
                viewDataBinding.ivBaseEmptyDefaultItmIcon.setImageDrawable(data.iconEmpty)
                viewDataBinding.tvBaseEmptyDefaultItmMessage.apply {
                    text = data.message
                    setTextColor(data.messageColor)
                }
            }
        }
    }

    class Loading

    class LoadMore(
        val message: String = STRING_DEFAULT,
        val messageColor: Int = getAppColor(R.color.base_color_gray),
        val progressBarColor: Int = getAppColor(R.color.base_color_gray),
        val overrideLayoutRes: Int? = null,
    )

    class Empty(
        val message: String = STRING_DEFAULT,
        val messageColor: Int = getAppColor(R.color.base_color_gray),
        val iconEmpty: Drawable? = getAppDrawable(R.drawable.default_photo),
        val overrideLayoutRes: Int? = null,
    )
}
