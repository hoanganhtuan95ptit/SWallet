package com.simple.wallet.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.simple.core.utils.extentions.asObject
import com.simple.wallet.data.cache.AppCache
import com.simple.wallet.data.cache.AppCacheImpl
import org.koin.dsl.module


val cacheModule = module {

    single {

        val context = get<Context>()

        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    single {
        AppCacheImpl(get()).asObject<AppCache>()
    }
}
