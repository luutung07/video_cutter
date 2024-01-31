package com.example.videocutter.domain.usecase

import com.example.library_base.common.BaseUseCase
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.domain.repo.IVideoRepo
import javax.inject.Inject

class GetFolderVideoUseCase @Inject constructor(
    private val iVideoRepo: IVideoRepo
) : BaseUseCase<BaseUseCase.VoidRequest, List<VideoInfo>>() {
    override suspend fun execute(rv: VoidRequest): List<VideoInfo> {
        return iVideoRepo.getFolder()
    }
}
