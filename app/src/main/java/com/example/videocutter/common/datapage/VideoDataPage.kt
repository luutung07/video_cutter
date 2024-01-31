package com.example.videocutter.common.datapage

import com.example.library_base.common.DataPage
import com.example.videocutter.AppConfig

class VideoDataPage<DATA> : DataPage<DATA>() {

    private var limitSize = AppConfig.PAGE_SIZE

    override fun hasLoadMore(): Boolean {
        return if (nextPageIndex == 0) {
            dataList.count() >= limitSize
        } else {
            dataList.count() / nextPageIndex >= limitSize
        }
    }
}
