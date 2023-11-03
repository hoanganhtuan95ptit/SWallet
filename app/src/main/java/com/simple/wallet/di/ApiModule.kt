package com.simple.wallet.di

import android.util.Log
import com.walletconnect.foundation.di.FoundationDITags
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule = module {

    single(named(FoundationDITags.OK_HTTP)) {

        Log.d("tuanha", "12345: ")

        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named(FoundationDITags.INTERCEPTOR)))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .writeTimeout(10 * 1000L, TimeUnit.MILLISECONDS)
            .readTimeout(10 * 1000L, TimeUnit.MILLISECONDS)
            .callTimeout(10 * 1000L, TimeUnit.MILLISECONDS)
            .connectTimeout(10 * 1000L, TimeUnit.MILLISECONDS)
            .build()
    }

    single {
        OkHttpClient
            .Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://github.com/")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(get())
            .build()
    }
}
