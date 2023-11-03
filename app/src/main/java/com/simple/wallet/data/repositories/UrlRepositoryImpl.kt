package com.simple.wallet.data.repositories

import com.simple.task.executeSyncByPriority
import com.simple.wallet.data.dao.UrlDao
import com.simple.wallet.data.task.url.UrlSyncTask
import com.simple.wallet.domain.entities.Url
import com.simple.wallet.domain.repositories.UrlRepository

class UrlRepositoryImpl(
    private val urlDao: UrlDao,
    private val urlSyncTaskList: List<UrlSyncTask>
) : UrlRepository {

    override suspend fun sync() {
        urlSyncTaskList.executeSyncByPriority(Unit)
    }

    override fun query(query: String): List<Url> {
        return urlDao.query(query.lowercase().trim())
    }

    override fun getUrlListBy(url: String): List<Url> {
        return urlDao.findListBy(url)
    }
}