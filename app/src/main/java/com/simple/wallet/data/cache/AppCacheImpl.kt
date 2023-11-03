package com.simple.wallet.data.cache

import android.content.SharedPreferences
import com.simple.coreapp.data.cache.sharedpreference.BaseCacheImpl

class AppCacheImpl(sharedPreferences: SharedPreferences) : BaseCacheImpl(sharedPreferences), AppCache