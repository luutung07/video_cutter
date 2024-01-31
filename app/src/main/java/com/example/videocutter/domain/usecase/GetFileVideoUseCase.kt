package com.example.videocutter.domain.usecase

import com.example.library_base.common.BaseUseCase
import com.example.library_base.extension.INT_DEFAULT
import com.example.videocutter.AppConfig
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.domain.repo.IVideoRepo
import javax.inject.Inject

class GetFileVideoUseCase @Inject constructor(private val iVideoRepo: IVideoRepo) :
    BaseUseCase<GetFileVideoUseCase.GetFileVideoRv, List<VideoInfo>>() {

    override suspend fun execute(rv: GetFileVideoRv): List<VideoInfo> {
        return iVideoRepo.getFiles(id = rv.id, page = rv.page, size = rv.size)
    }

    class GetFileVideoRv(
        val id: Long?
    ) : BaseUseCase.RequestValue {
        var size = AppConfig.PAGE_SIZE
        var page = INT_DEFAULT
    }
}
