package com.example.library_base.common

import com.example.library_base.utils.AppConfig

open class DataPage<DATA> {
    var dataList: MutableList<DATA> = mutableListOf()
        private set
    var nextPageIndex: Int = 0
        private set
    var limit: Int = AppConfig.PAGE_LIMIT_DEFAULT
        private set

    fun addList(list: List<DATA>?) {
        list?.let {
            this.dataList.addAll(it)
            this.nextPageIndex += 1
        }
    }

    fun addList(list: List<DATA>?, isResetPageIndex: Boolean = false) {
        list?.let {
            if (isResetPageIndex) {
                this.dataList.addAll(it)
            } else {
                addList(list)
            }
        }
    }

    @Deprecated("lỗi tham chiếu, muốn sử dụng phải clone list chứ k được dùng trực tiếp")
    fun replaceCurrentList(list: List<DATA>?) {
        list?.let {
            dataList.clear()
            dataList.addAll(it)
        }
    }

    fun updateData(position: Int, item: DATA) {
        this.dataList[position] = item
    }

    fun copyList(list: List<DATA>?) {
        list?.let {
            this.dataList.addAll(it)
        }
    }

    fun add(data: DATA) {
        this.dataList.add(data)
    }

    fun add(index: Int, data: DATA) {
        this.dataList.add(index, data)
    }

    fun count(): Int {
        return dataList.count()
    }

    fun reset() {
        this.nextPageIndex = 0
        this.dataList.clear()
    }

    open fun hasLoadMore(): Boolean {
        return if (nextPageIndex > 0) {
            (dataList.count() / nextPageIndex) >= limit
        } else {
            dataList.size >= limit
        }
    }
}
