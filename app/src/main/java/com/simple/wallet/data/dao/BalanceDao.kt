package com.simple.wallet.data.dao

import androidx.room.*
import com.simple.wallet.domain.entities.Balance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.math.BigInteger

private const val TABLE_NAME = "balances"

@Dao
interface BalanceDao {


    fun getListByAsync(walletAddressList: List<String>): Flow<List<Balance>> = getRoomListByAsync(walletAddressList).distinctUntilChanged().toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE walletAddress COLLATE NOCASE = :walletAddressList AND balance != '0'")
    fun getRoomListByAsync(walletAddressList: List<String>): Flow<List<RoomBalance>>


    fun getListByAsync(chainIdList: List<Long>, walletAddressList: List<String>): Flow<List<Balance>> = getRoomListByAsync(chainIdList, walletAddressList).distinctUntilChanged().toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND walletAddress COLLATE NOCASE = :walletAddressList AND balance != '0'")
    fun getRoomListByAsync(chainIdList: List<Long>, walletAddressList: List<String>): Flow<List<RoomBalance>>


    fun getListBy(chainIdList: List<Long>, walletAddressList: List<String>, tokenAddressList: List<String>): List<Balance> = getRoomListBy(chainIdList, walletAddressList, tokenAddressList).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND walletAddress COLLATE NOCASE = :walletAddressList AND tokenAddress COLLATE NOCASE IN (:tokenAddressList)")
    fun getRoomListBy(chainIdList: List<Long>, walletAddressList: List<String>, tokenAddressList: List<String>): List<RoomBalance>


    fun insert(vararg entity: Balance) = insertOrUpdate(entity.map { it.toRoom() })

    fun insert(entities: List<Balance>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomBalance>)


    @Query("DELETE FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND walletAddress COLLATE NOCASE IN (:walletAddressList)")
    fun deleteBy(chainIdList: List<Long>, walletAddressList: List<String>)

    @Query("DELETE FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND walletAddress COLLATE NOCASE IN (:walletAddressList) AND tokenAddress COLLATE NOCASE IN (:tokenAddressList)")
    fun deleteBy(chainIdList: List<Long>, walletAddressList: List<String>, tokenAddressList: List<String>)
}


@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["chainId", "tokenAddress", "walletAddress"],
)
data class RoomBalance(

    val chainId: Long,
    var tokenAddress: String = "",
    var walletAddress: String = "",

    var balance: String,
)

private fun List<Balance>.toRoom() = map {

    it.toRoom()
}

private fun Balance.toRoom(): RoomBalance {

    return RoomBalance(
        chainId = chainId,
        tokenAddress = tokenAddress,
        walletAddress = walletAddress,

        balance = balance.toString(),
    )
}

private fun Flow<List<RoomBalance>>.toEntity() = map {

    it.toEntity()
}

private fun List<RoomBalance>.toEntity() = map {

    it.toEntity()
}

private fun RoomBalance.toEntity(): Balance {

    return Balance(

        chainId = chainId,
        tokenAddress = tokenAddress,
        walletAddress = walletAddress,

        balance = balance.toBigIntegerOrNull() ?: BigInteger.ZERO,
    )
}