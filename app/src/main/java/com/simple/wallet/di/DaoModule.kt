package com.simple.wallet.di

import androidx.room.Room
import com.simple.wallet.data.dao.WalletRoomDatabase
import org.koin.dsl.module

val daoModule = module {

    single {
        Room.databaseBuilder(get(), WalletRoomDatabase::class.java, "wallet_database")
            .build()
    }

    single {
        get<WalletRoomDatabase>().providerWalletDao()
    }
}