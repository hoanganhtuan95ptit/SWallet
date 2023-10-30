package com.simple.wallet.data.dao.chain

import androidx.room.*
import com.simple.wallet.domain.entities.Chain

private const val TABLE_NAME = "smart_contracts"

@Dao
interface SmartContractDao {


    fun getListByChainIdAndTypes(chainId: Long, types: List<String>): List<Chain.SmartContract> = getRoomListByChainIdAndTypes(chainId, types).toEntity()

    @Query("SELECT * from $TABLE_NAME WHERE chainId = :chainId AND type IN (:types)")
    fun getRoomListByChainIdAndTypes(chainId: Long, types: List<String>): List<RoomSmartContract>


    fun insert(vararg entity: Chain.SmartContract) = insertOrUpdate(entity.map { it.toRoom() })

    fun insert(entities: List<Chain.SmartContract>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomSmartContract>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["chainId", "address", "type"],
)
data class RoomSmartContract(

    val chainId: Long,

    var type: String = "",
    var address: String = "",
)

private fun List<Chain.SmartContract>.toRoom() = map {

    it.toRoom()
}

private fun Chain.SmartContract.toRoom(): RoomSmartContract {

    return RoomSmartContract(
        chainId = chainId,

        type = type,
        address = address,
    )
}

private fun List<RoomSmartContract>.toEntity() = map {

    it.toEntity()
}

private fun RoomSmartContract.toEntity(): Chain.SmartContract {

    return Chain.SmartContract(
        chainId = chainId,

        type = type,
        address = address,
    )
}