package com.simple.wallet.data.dao.wallet

import androidx.room.Database
import androidx.room.RoomDatabase

private const val versionDao = 1

@Database(entities = [RoomWallet::class], version = versionDao, exportSchema = false)
abstract class WalletRoomDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
}