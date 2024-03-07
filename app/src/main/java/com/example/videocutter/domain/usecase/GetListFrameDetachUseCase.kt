package com.example.videocutter.domain.usecase

import android.graphics.Bitmap
import com.example.library_base.common.BaseUseCase
import com.example.videocutter.domain.repo.IVideoRepo
import javax.inject.Inject

class GetListFrameDetachUseCase @Inject constructor(
    private val repo: IVideoRepo
    ) : BaseUseCase<GetListFrameDetachUseCase.GetListFrameDetachRV, List<Bitmap>>() {

    override suspend fun execute(rv: GetListFrameDetachRV): List<Bitmap> {
        return repo.getFrameDetach(rv.listPath)
    }

    class GetListFrameDetachRV(val listPath: List<String>) : BaseUseCase.RequestValue
}
