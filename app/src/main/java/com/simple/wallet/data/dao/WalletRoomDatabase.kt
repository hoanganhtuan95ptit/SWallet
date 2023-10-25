package com.simple.wallet.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.simple.core.utils.extentions.toJson
import com.simple.core.utils.extentions.toObjectOrNull

const val versionDao = 1

@Database(entities = [RoomWallet::class], version = versionDao, exportSchema = false)
@TypeConverters(ListStringConverter::class, HashMapConverter::class)
abstract class WalletRoomDatabase : RoomDatabase() {

    abstract fun providerWalletDao(): WalletDao
}

object ListStringConverter {

    @JvmStatic
    @TypeConverter
    fun listStringToString(strings: List<String>): String = strings.joinToString { it }

    @JvmStatic
    @TypeConverter
    fun stringToListString(concatenatedStrings: String): List<String> = concatenatedStrings.split(",")
}

object HashMapConverter {

    @JvmStatic
    @TypeConverter
    fun hashMapToString(strings: HashMap<String, List<String>>): String = strings.toJson()

    @JvmStatic
    @TypeConverter
    fun stringToHashMap(concatenatedStrings: String): HashMap<String, List<String>> = concatenatedStrings.toObjectOrNull<HashMap<String, List<String>>>() ?: hashMapOf()
}