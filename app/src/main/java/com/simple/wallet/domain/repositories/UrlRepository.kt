package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Url

interface UrlRepository {

    fun getUrlListBy(url: String): List<Url>
}