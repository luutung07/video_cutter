package com.example.videocutter.data.repo

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.INT_DEFAULT
import com.example.library_base.extension.STRING_DEFAULT
import com.example.library_base.extension.getApplication
import com.example.videocutter.R
import com.example.videocutter.data.local.contenprovider.VideoInfoDTO
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.domain.repo.IVideoRepo
import javax.inject.Inject

class VideoRepoImpl @Inject constructor() : IVideoRepo {
    companion object {
        private val LIBRARY_NAME = getAppString(R.string.library_title)
    }

    override fun getFolder(): List<VideoInfo> {
        val mapDataFolder: LinkedHashMap<String?, VideoInfoDTO> = LinkedHashMap()
        var totalCount = INT_DEFAULT
        var imagePathLibrary = STRING_DEFAULT

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.MIME_TYPE,
        )
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC"

        val queryUri = MediaStore.Files.getContentUri("external")
        val contentResolver: ContentResolver = getApplication().contentResolver
        val cursor =
            contentResolver.query(queryUri, projection, selection, selectionArgs, sortOrder)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val idFolder =
                    cursor.getLongOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID))
                val name =
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))
                val imagePath =
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                val duration =
                    cursor.getLongOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION))
                val mimeType =
                    cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))

                if (imagePathLibrary == STRING_DEFAULT) {
                    imagePathLibrary = imagePath.toString()
                }

                // folder
                if (mapDataFolder.containsKey(name)) {
                    val valueCurrent = mapDataFolder[name]!!
                    if (valueCurrent.count != null) {
                        valueCurrent.count = valueCurrent.count!! + 1
                    }
                    mapDataFolder[name] = valueCurrent
                } else {
                    mapDataFolder[name] = VideoInfoDTO(
                        id = idFolder,
                        name = name,
                        thumbnailUrl = imagePath,
                        count = 1
                    )
                }
            }
            cursor.close()
        }

        mapDataFolder.forEach { k, v ->
            totalCount += v.count ?: 0
        }

        val data = mapDataFolder.values.toMutableList()
        data.add(
            0,
            VideoInfoDTO(
                id = null,
                name = LIBRARY_NAME,
                count = totalCount,
                thumbnailUrl = imagePathLibrary
            )
        )

        val result = data.map {
            VideoInfo(
                id = it.id,
                name = it.name,
                count = it.count,
                thumbnailUrl = it.thumbnailUrl,
                createTime = it.createTime,
                duration = it.duration
            )
        }
        return result
    }

    override fun getFiles(id: Long?, page: Int, size: Int): List<VideoInfo> {
        val startPage = (page - 1) * size
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_ADDED
        )
        var whereCondition: String? = null
        var selectionArgs: Array<String>? = null

        if (id == null) {
            whereCondition =
                "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
            selectionArgs = arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
            )
        } else {
            whereCondition = MediaStore.MediaColumns.BUCKET_ID + " = ? AND (" +
                    MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? )";
            selectionArgs = arrayOf(
                id.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
            )
        }

        val list: MutableList<VideoInfoDTO> = arrayListOf()
        createCursor(
            contentResolver = getApplication().contentResolver,
            collection = collection,
            projection = projection,
            whereCondition = whereCondition,
            selectionArgs = selectionArgs,
            orderBy = MediaStore.Audio.Media.DATE_ADDED,
            orderAscending = false,
            limit = size,
            offset = startPage
        )?.use { cursor ->

            while (cursor.moveToNext()) {
                val imagePath =
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                val duration =
                    cursor.getLongOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION))
                val idFile =
                    cursor.getLongOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val nameFile =
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                val createTime =
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))

                list.add(
                    VideoInfoDTO(
                        id = idFile,
                        name = nameFile,
                        thumbnailUrl = imagePath,
                        duration = duration,
                        createTime = createTime
                    )
                )
            }
            cursor.close()
        }

        val result = list.map {
            VideoInfo(
                id = it.id,
                name = it.name,
                count = it.count,
                thumbnailUrl = it.thumbnailUrl,
                createTime = it.createTime,
                duration = it.duration
            )
        }

        return result
    }

    private fun createCursor(
        contentResolver: ContentResolver,
        collection: Uri,
        projection: Array<String>,
        whereCondition: String,
        selectionArgs: Array<String>,
        orderBy: String,
        orderAscending: Boolean,
        limit: Int,
        offset: Int
    ): Cursor? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            val selection = createSelectionBundle(
                whereCondition,
                selectionArgs,
                orderBy,
                orderAscending,
                limit,
                offset
            )
            contentResolver.query(collection, projection, selection, null)
        }

        else -> {
            val orderDirection = if (orderAscending) "ASC" else "DESC"
            var order = when (orderBy) {
                "ALPHABET" -> "${MediaStore.Audio.Media.TITLE}, ${MediaStore.Audio.Media.ARTIST} $orderDirection"
                else -> "${MediaStore.Audio.Media.DATE_ADDED} $orderDirection"
            }
            order += " LIMIT $limit OFFSET $offset"
            contentResolver.query(collection, projection, whereCondition, selectionArgs, order)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSelectionBundle(
        whereCondition: String,
        selectionArgs: Array<String>,
        orderBy: String,
        orderAscending: Boolean,
        limit: Int,
        offset: Int
    ): Bundle = Bundle().apply {
        // Limit & Offset
        putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
        putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
        // Sort function
        when (orderBy) {
            "ALPHABET" -> putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.Files.FileColumns.TITLE)
            )

            else -> putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
            )
        }
        // Sorting direction
        val orderDirection =
            if (orderAscending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
        // Selection
        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, whereCondition)
        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
    }
}
