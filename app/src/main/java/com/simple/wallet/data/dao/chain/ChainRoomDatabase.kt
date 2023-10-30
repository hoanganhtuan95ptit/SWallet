package com.simple.wallet.data.dao.chain

import androidx.room.Database
import androidx.room.RoomDatabase

private const val versionDao = 1

@Database(entities = [RoomRpc::class, RoomChain::class, RoomSmartContract::class], version = versionDao, exportSchema = false)
abstract class ChainRoomDatabase : RoomDatabase() {

    abstract fun chainDao(): ChainDao

    abstract fun rpcChainDao(): RpcChainDao

    abstract fun smartContractDao(): SmartContractDao
}