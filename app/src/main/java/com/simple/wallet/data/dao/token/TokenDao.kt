package com.simple.wallet.data.dao.token

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Token.Companion.toTokenTag
import com.simple.wallet.domain.entities.Token.Companion.toTokenType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val TABLE_NAME = "tokens"

@Dao
interface TokenDao {


    fun findListBy(vararg tokenType: Token.Type): List<Token> = findRoomListByType(tokenType.map { it.value }).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE type COLLATE NOCASE IN (:tokenType)")
    fun findRoomListByType(tokenType: List<String>): List<RoomToken>


    fun findListByAsync(vararg tokenType: Token.Type): Flow<List<Token>> = findRoomListByTypeAsync(tokenType.map { it.value }).distinctUntilChanged().toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE type COLLATE NOCASE IN (:tokenType)")
    fun findRoomListByTypeAsync(tokenType: List<String>): Flow<List<RoomToken>>


    fun findListBy(chainId: List<Long>, vararg tokenType: Token.Type): List<Token> = findRoomListByChainIdAndType(chainId, tokenType.map { it.value }).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainId) AND type COLLATE NOCASE IN (:tokenType)")
    fun findRoomListByChainIdAndType(chainId: List<Long>, tokenType: List<String>): List<RoomToken>


    fun findListBy(chainId: List<Long>, tokenAddress: List<String>, vararg tokenType: Token.Type): List<Token> = findRoomListBy(chainId, tokenAddress, tokenType.map { it.value }).toEntity()

    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainId) AND address IN (:tokenAddress) AND type COLLATE NOCASE IN (:tokenType)")
    fun findRoomListBy(chainId: List<Long>, tokenAddress: List<String>, tokenType: List<String>): List<RoomToken>


    fun insert(vararg entity: Token) = insertOrUpdate(entity.toList().toRoom())

    fun insert(entities: List<Token>) = insertOrUpdate(entities.toRoom())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomToken>)


    @Query("DELETE FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND address COLLATE NOCASE IN (:tokenAddressList)")
    fun delete(chainIdList: List<Long>, tokenAddressList: List<String>)
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
        tag = tag.value,
        type = type.value,
        geckoId = geckoId
    )
}

private fun Flow<List<RoomToken>>.toEntity() = map {

    it.toEntity()
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
        tag = tag.toTokenTag(),
        type = type.toTokenType(),
        geckoId = geckoId
    )
}