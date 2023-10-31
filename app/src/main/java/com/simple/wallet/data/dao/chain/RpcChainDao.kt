package com.simple.wallet.data.dao.chain

import androidx.room.*
import com.simple.wallet.domain.entities.Chain

private const val TABLE_NAME = "rpcs"

@Dao
interface RpcChainDao {

    fun findListBy(chainId: Long, limit: Int): List<Chain.Rpc> = getRoomListBy(chainId, limit).toEntity()

    @Query("SELECT * from $TABLE_NAME WHERE chainId = :chainId ORDER BY priority DESC LIMIT :limit")
    fun getRoomListBy(chainId: Long, limit: Int): List<RoomRpc>


    fun insert(vararg entity: Chain.Rpc) = insertOrUpdate(entity.map { it.toRoom() })

    fun insert(entities: List<Chain.Rpc>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomRpc>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["chainId", "priority"],
)
data class RoomRpc(
    val chainId: Long,
    var priority: Int = 0,

    var url: String = "",
    var name: String = "",
)

private fun List<Chain.Rpc>.toRoom() = map {

    it.toRoom()
}

private fun Chain.Rpc.toRoom(): RoomRpc {

    return RoomRpc(
        chainId = chainId,
        priority = priority,

        url = url,
        name = name,
    )
}

private fun List<RoomRpc>.toEntity() = map {

    it.toEntity()
}

private fun RoomRpc.toEntity(): Chain.Rpc {

    return Chain.Rpc(
        chainId = chainId,
        priority = priority,

        url = url,
        name = name,
    )
}