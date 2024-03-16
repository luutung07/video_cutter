package com.example.videocutter.domain.usecase

import com.example.library_base.common.BaseUseCase
import com.example.videocutter.domain.model.Music
import com.example.videocutter.domain.repo.IMusicRepo
import com.example.videocutter.presentation.display.model.music.MUSIC_TYPE
import javax.inject.Inject

class GetListMusicUseCase @Inject constructor(
    private val repo: IMusicRepo
) : BaseUseCase<GetListMusicUseCase.GetListMusicRV, List<Music>>() {

    override suspend fun execute(rv: GetListMusicRV): List<Music> {
        return if (rv.type == MUSIC_TYPE.ITUNES) {
            repo.getITunesList()
        } else {
            repo.getListMusicLocal()
        }
    }

    class GetListMusicRV(val type: MUSIC_TYPE) : BaseUseCase.RequestValue
}
