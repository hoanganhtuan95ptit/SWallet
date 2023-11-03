package com.simple.wallet.data.task.url

import android.content.Context
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.core.utils.extentions.toListOrEmpty
import com.simple.wallet.R
import com.simple.wallet.data.cache.AppCache
import com.simple.wallet.data.dao.UrlDao
import com.simple.wallet.domain.entities.Url
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class DefaultUrlSyncTask(
    private val context: Context,
    private val appCache: AppCache,

    private val urlDao: UrlDao,
) : UrlSyncTask {

    private val version = 1L

    override suspend fun executeTask(param: Unit) {

        val versionCache = appCache.getLong(DEFAULT_URL_SYNC_TASK) ?: 0

        if (version <= versionCache) return

        readTextFile(context.resources.openRawResource(R.raw.dapp)).toListOrEmpty<UrlResponse>().apply {

            appCache.putLong(DEFAULT_URL_SYNC_TASK, version)
        }.map {

            Url(
                url = it.url,

                name = it.name,

                image = it.logo,

                description = it.description ?: "",

                tag = Url.Tag.VERIFIED
            )
        }.let {

            urlDao.insert(it)
        }
    }

    private fun readTextFile(inputStream: InputStream): String {

        val outputStream = ByteArrayOutputStream()

        val buf = ByteArray(1024)

        var len: Int

        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (_: IOException) {
        }

        return outputStream.toString()
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class UrlResponse(
        var url: String = "",

        var name: String = "",

        var logo: String = "",

        var description: String? = "",
    )

    companion object {

        private const val DEFAULT_URL_SYNC_TASK = "DEFAULT_URL_SYNC_TASK"
    }
}