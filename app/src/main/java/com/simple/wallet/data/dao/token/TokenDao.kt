package com.simple.wallet.data.dao.token

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Token.Companion.toTokenTag
import com.simple.wallet.domain.entities.Token.Companion.toTokenType
import java.math.BigDecimal

private const val TABLE_NAME = "tokens"

@Dao
interface TokenDao {


    fun insert(vararg entity: Token) = insertOrUpdate(entity.toList().toRoom())

    fun insert(entities: List<Token>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomToken>)


    @Query("DELETE FROM $TABLE_NAME WHERE chainId = :chainId AND address COLLATE NOCASE = :tokenAddress")
    fun delete(chainId: Long, tokenAddress: String)

    @Query("DELETE FROM $TABLE_NAME WHERE chainId = :chainId AND address COLLATE NOCASE IN (:tokenAddressList)")
    fun delete(chainId: Long, tokenAddressList: List<String>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["chainId", "address"],
)
data class RoomToken(
    var address: String = "",

    var symbol: String = "",

    var name: String = "",

    var decimals: Int = 0,

    var logo: String = "",

    var chainId: Long = 0,

    var price: String = BigDecimal.ZERO.toPlainString(),

    var tag: String = Token.Tag.UNKNOWN.value,

    var type: String = Token.Type.ERC_20.value,

    var geckoId: String = ""
)


private fun List<Token>.toRoom() = map {

    it.toRoom()
}

private fun Token.toRoom(): RoomToken {

    return RoomToken(
        address = address,
        symbol = symbol,
        name = name,
        decimals = decimals,
        logo = logo,
        chainId = chainId,
        price = price.toPlainString(),
        tag = tag.value,
        type = type.value,
        geckoId = geckoId
    )
}

private fun List<RoomToken>.toEntity() = map {

    it.toEntity()
}

private fun RoomToken.toEntity(): Token {

    return Token(
        address = address,
        symbol = symbol,
        name = name,
        decimals = decimals,
        logo = logo,
        chainId = chainId,
        price = price.toBigDecimal(),
        tag = tag.toTokenTag(),
        type = type.toTokenType(),
        geckoId = geckoId
    )
}