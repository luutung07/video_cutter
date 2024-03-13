package com.example.videocutter.domain.usecase

import com.example.library_base.common.BaseUseCase
import com.example.videocutter.domain.model.ITunes
import com.example.videocutter.domain.repo.IMusicRepo
import javax.inject.Inject

class GetItunesListUseCase @Inject constructor(
    private val repo: IMusicRepo
) : BaseUseCase<BaseUseCase.VoidRequest, List<ITunes>>() {
    override suspend fun execute(rv: VoidRequest): List<ITunes> {
        return repo.getITunesList()
    }
}
