package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Url

interface UrlRepository {

    suspend fun sync()

    fun query(query: String): List<Url>

    fun getUrlListBy(url: String): List<Url>
}