package com.simple.wallet.data.dao.chain

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.core.utils.extentions.toJson
import com.simple.core.utils.extentions.toObjectOrNull
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Chain.Companion.toChainType

private const val TABLE_NAME = "chains"

@Dao
interface ChainDao {

    fun findBy(id: Long) = findRoomById(id).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun findRoomById(id: Long): RoomChain


    fun findListBy(id: Long) = findRoomListById(id).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun findRoomListById(id: Long): List<RoomChain>


    fun findListBy(vararg types: Chain.Type) = findRoomListByType(types.map { it.value }).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE type IN (:types)")
    fun findRoomListByType(types: List<String>): List<RoomChain>


    fun insert(vararg entity: Chain) = insertOrUpdate(entity.toList().map { it.toRoom() })

    fun insert(entities: List<Chain>) = insertOrUpdate(entities.toRoom())


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomChain>)

    @Query("DELETE FROM $TABLE_NAME WHERE id COLLATE NOCASE IN (:idList)")
    fun deleteBy(idList: List<String>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["id"]
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class RoomChain(

    val id: Long,

    var name: String = "",

    var image: String = "",

    var type: String = "",

    var explorer: String = "",

    var config: String = ""
)

private fun List<Chain>.toRoom() = map {

    it.toRoom()
}

private fun Chain.toRoom(): RoomChain {

    return RoomChain(
        id = id,
        name = name,
        image = image,
        type = type.value,
        explorer = explorer.toJson(),
        config = config.toJson()
    )
}

private fun List<RoomChain>.toEntity() = map {

    it.toEntity()
}

private fun RoomChain.toEntity(): Chain {

    return Chain(
        id = id,
        name = name,
        image = image,
        type = type.toChainType(),
        explorer = explorer.toObjectOrNull<Chain.Explorer>(),
        config = config.toObjectOrNull<Map<Chain.Config, String>>()
    )
}