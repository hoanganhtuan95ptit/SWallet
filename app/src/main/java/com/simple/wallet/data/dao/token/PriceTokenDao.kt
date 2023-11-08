package com.simple.wallet.data.dao.token

import androidx.room.*
import com.simple.core.utils.extentions.toJson
import com.simple.core.utils.extentions.toObject
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Token.Companion.toTokenPriceChange
import com.simple.wallet.utils.exts.toBigDecimalOrZero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TABLE_NAME = "prices"

@Dao
interface PriceTokenDao {


    fun findListBy(chainId: List<Long>, tokenAddress: List<String>): List<Token.Price> = findRoomListBy(chainId, tokenAddress).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainId) AND address IN (:tokenAddress)")
    fun findRoomListBy(chainId: List<Long>, tokenAddress: List<String>): List<RoomTokenPrice>


    fun findListByAsync(chainId: List<Long>, tokenAddress: List<String>): Flow<List<Token.Price>> = findRoomListByAsync(chainId, tokenAddress).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainId) AND address IN (:tokenAddress)")
    fun findRoomListByAsync(chainId: List<Long>, tokenAddress: List<String>): Flow<List<RoomTokenPrice>>


    fun insert(vararg entity: Token.Price) = insertOrUpdate(entity.toList().toRoom())

    fun insert(entities: List<Token.Price>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomTokenPrice>)


    @Query("DELETE FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND address COLLATE NOCASE IN (:tokenAddressList)")
    fun delete(chainIdList: List<Long>, tokenAddressList: List<String>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["chainId", "address"],
)
data class RoomTokenPrice(

    val chainId: Long,
    var address: String = "",

    var price: String,
    var priceChange: String
)


private fun List<Token.Price>.toRoom() = map {

    it.toRoom()
}

private fun Token.Price.toRoom(): RoomTokenPrice {

    return RoomTokenPrice(
        chainId = chainId,
        address = address,

        price = price.toPlainString(),
        priceChange = priceChange.toJson()
    )
}


private fun Flow<List<RoomTokenPrice>>.toEntity() = map {

    it.toEntity()
}

private fun List<RoomTokenPrice>.toEntity() = map {

    it.toEntity()
}

private fun RoomTokenPrice.toEntity(): Token.Price {

    return Token.Price(
        chainId = chainId,
        address = address,

        price = price.toBigDecimalOrZero(),
        priceChange = priceChange.toObject<Map<String, String>>().mapKeys { it.key.toTokenPriceChange() }.mapValues { it.value.toBigDecimalOrZero() }
    )
}