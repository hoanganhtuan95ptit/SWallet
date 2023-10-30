package com.simple.wallet.data.dao.token

import androidx.room.Database
import androidx.room.RoomDatabase

private const val versionDao = 1

@Database(entities = [RoomToken::class], version = versionDao, exportSchema = false)
abstract class TokenRoomDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao
}