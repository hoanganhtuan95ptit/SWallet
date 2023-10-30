package com.simple.wallet.data.dao.walletconnect

import androidx.room.Database
import androidx.room.RoomDatabase

private const val versionDao = 1

@Database(entities = [RoomWalletConnectSession::class], version = versionDao, exportSchema = false)
abstract class WalletConnectSessionDaoRoomDatabase : RoomDatabase() {

    abstract fun walletConnectSessionDao(): WalletConnectSessionDao
}
//
//internal val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN slide TEXT NOT NULL DEFAULT ''")
//    }
//}
//
//internal val MIGRATION_2_3: Migration = object : Migration(2, 3) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE $TABLE_NAME RENAME COLUMN slide TO connectSource")
//        database.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN pairToken TEXT NOT NULL DEFAULT ''")
//    }
//}