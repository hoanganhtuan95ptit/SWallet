package com.simple.wallet.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Chain.Companion.toChainType
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.entities.Wallet.Companion.toWalletType

private const val TABLE_NAME = "wallets"

@Dao
interface WalletDao {

    fun findWalletListBy(vararg walletAddress: String): List<Wallet> = findRoomWalletListByAddress(walletAddress.toList()).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE address IN (:walletAddressList)")
    fun findRoomWalletListByAddress(walletAddressList: List<String>): List<RoomWallet>


    fun findWalletListBy(walletAddress: Map<String, Chain.Type>): List<Wallet> = walletAddress.flatMap { findRoomWalletListBy(it.key, it.value) }.toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE address == :walletAddress AND chainType == :chainType")
    fun findRoomWalletListBy(walletAddress: String, chainType: Chain.Type): List<RoomWallet>


    fun findWalletListBy(vararg walletType: Wallet.Type): List<Wallet> = findRoomWalletListByType(walletType.toList().map { it.value }).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE walletType IN (:walletTypeList)")
    fun findRoomWalletListByType(walletTypeList: List<String>): List<RoomWallet>


    fun insert(wallet: Wallet) = insert(wallet.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: List<RoomWallet>)


    @Query("DELETE FROM $TABLE_NAME WHERE id COLLATE NOCASE IN (:idList)")
    fun deleteBy(idList: List<String>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["id", "address", "chainType"]
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class RoomWallet(

    var id: String = "",

    var name: String = "",

    var address: String = "",

    var chainType: String = "",

    var walletType: String = ""
)

private fun Wallet.toRoom() = addressMap.map {

    RoomWallet(
        id ?: "",
        name,
        address = it.key,
        chainType = it.value.value,
        walletType = type.value
    )
}

private fun List<RoomWallet>.toEntity() = groupBy { it.id }.map { entry ->

    val first = entry.value.first()

    Wallet(
        first.id,
        first.name,
        first.walletType.toWalletType(),
        addressMap = entry.value.associateBy({ it.address }, { it.chainType.toChainType() })
    )
}