package com.simple.wallet.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

private const val versionDao = 1

@Database(entities = [RoomUrl::class, RoomBalance::class], version = versionDao, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun urlDao(): UrlDao

    abstract fun balanceDao(): BalanceDao
}