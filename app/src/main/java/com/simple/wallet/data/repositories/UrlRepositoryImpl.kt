package com.simple.wallet.data.repositories

import com.simple.wallet.data.dao.UrlDao
import com.simple.wallet.domain.entities.Url
import com.simple.wallet.domain.repositories.UrlRepository

class UrlRepositoryImpl(
    private val urlDao: UrlDao
): UrlRepository {

    override fun getUrlListBy(url: String): List<Url> {
        return urlDao.findListBy(url)
    }
}