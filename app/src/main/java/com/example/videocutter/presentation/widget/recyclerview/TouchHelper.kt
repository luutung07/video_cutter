package com.example.videocutter.presentation.widget.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class TouchHelper : ItemTouchHelper.Callback() {

    private val TAG = "ItemTouchHelperCustom"

    abstract val dataList: MutableList<Any>?

    abstract val isDragItemLast: Boolean

    abstract fun eventMove(oldIndex: Int, newIndex: Int)

    open fun onSwipe(onSwipe: (() -> Unit)?) {
        this.onSwipe = onSwipe
    }

    private var onSwipe: (() -> Unit)? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        if (dataList == null) return 0
        return when (isDragItemLast) {
            true -> {
                makeMovementFlags(dragFlags, 0)
            }

            false -> {
                if (viewHolder.absoluteAdapterPosition != dataList!!.size - 1) {
                    makeMovementFlags(dragFlags, 0)
                } else {
                    0
                }
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val dragIndex = viewHolder.bindingAdapterPosition
        val targetIndex = target.bindingAdapterPosition

        return when (isDragItemLast) {
            true -> {
                this.eventMove(dragIndex, targetIndex)
                true
            }

            false -> {
                if (targetIndex != dataList!!.size - 1) {
                    this.eventMove(dragIndex, targetIndex)
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipe?.invoke()
    }
}
