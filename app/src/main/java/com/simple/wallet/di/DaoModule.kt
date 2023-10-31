package com.simple.wallet.di

import androidx.room.Room
import com.simple.wallet.data.dao.AppRoomDatabase
import com.simple.wallet.data.dao.chain.ChainRoomDatabase
import com.simple.wallet.data.dao.token.TokenRoomDatabase
import com.simple.wallet.data.dao.wallet.WalletRoomDatabase
import com.simple.wallet.data.dao.walletconnect.WalletConnectSessionDaoRoomDatabase
import org.koin.dsl.module

val daoModule = module {

    single {
        Room.databaseBuilder(get(), AppRoomDatabase::class.java, "app_database")
            .build()
    }

    single {
        get<AppRoomDatabase>().urlDao()
    }


    single {
        Room.databaseBuilder(get(), WalletRoomDatabase::class.java, "wallet_database")
            .build()
    }

    single {
        get<WalletRoomDatabase>().walletDao()
    }


    single {
        Room.databaseBuilder(get(), ChainRoomDatabase::class.java, "chain_database")
            .build()
    }

    single {
        get<ChainRoomDatabase>().chainDao()
    }

    single {
        get<ChainRoomDatabase>().rpcChainDao()
    }

    single {
        get<ChainRoomDatabase>().smartContractDao()
    }


    single {
        Room.databaseBuilder(get(), TokenRoomDatabase::class.java, "token_database")
            .build()
    }

    single {
        get<TokenRoomDatabase>().tokenDao()
    }


    single {
        Room.databaseBuilder(get(), WalletConnectSessionDaoRoomDatabase::class.java, "wallet_connect_database")
            .build()
    }

    single {
        get<WalletConnectSessionDaoRoomDatabase>().walletConnectSessionDao()
    }
}