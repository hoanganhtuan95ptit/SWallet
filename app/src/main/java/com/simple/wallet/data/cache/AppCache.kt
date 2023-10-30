package com.simple.wallet.data.cache

import com.simple.coreapp.data.cache.BaseCache

interface AppCache : BaseCache {

    fun getVersionCachePhonetics(): Long

    fun saveVersionCachePhonetics(version: Long)
}