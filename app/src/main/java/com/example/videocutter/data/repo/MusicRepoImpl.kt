package com.example.videocutter.data.repo

import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.library_base.extension.getApplication
import com.example.videocutter.data.local.contenprovider.MusicDTO
import com.example.videocutter.data.remote.server.ITunesService
import com.example.videocutter.domain.model.Music
import com.example.videocutter.domain.repo.IMusicRepo
import javax.inject.Inject


class MusicRepoImpl @Inject constructor(
    private val iTunesService: ITunesService
) : IMusicRepo {
    override fun getITunesList(): List<Music> {
        return try {
            val response = iTunesService.getItunes().execute()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!.results?.map {
                    Music(
                        name = it.trackName,
                        id = it.trackId.toString(),
                        url = it.previewUrl,
                        duration = it.trackTimeMillis
                    )
                }
                result ?: arrayListOf()
            } else {
                throw Exception(response.message())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getListMusicLocal(): List<Music> {
        val listMusic: MutableList<MusicDTO> = arrayListOf()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor = getApplication().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name =
                    cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val path =
                    cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val id = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val duration =
                    cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                listMusic.add(
                    MusicDTO(id = id, name = name, url = path, duration = duration)
                )
            }
            cursor.close()
        }
        val result = listMusic.map {
            Music(
                id = it.id,
                name = it.name,
                url = it.url,
                duration = it.duration
            )
        }
        return result
    }
}
