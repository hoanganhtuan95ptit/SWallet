package com.simple.wallet.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.wallet.domain.entities.WalletConnectSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private const val TABLE_NAME = "wallet_connect_session"

@Dao
interface WalletConnectSessionDao {

    fun getListAsync() = getRoomListAsync().map { it.map { it.toEntity() } }


    fun getListByTopic(topic: String) = getRoomListByTopic(topic).map { it.toEntity() }

    fun getListByPairingTopic(pairingTopic: String) = getRoomListByPairingTopic(pairingTopic).map { it.toEntity() }


    fun getListByTopicAsync(topic: String) = getRoomListByTopicAsync(topic).map { it.map { it.toEntity() } }


    @Query("SELECT * FROM $TABLE_NAME WHERE 1=1")
    fun getRoomListAsync(): Flow<List<RoomWalletConnectSession>>


    @Query("SELECT * FROM $TABLE_NAME WHERE topic==:topic")
    fun getRoomListByTopic(topic: String): List<RoomWalletConnectSession>

    @Query("SELECT * FROM $TABLE_NAME WHERE pairToken==:pairToken")
    fun getRoomListByPairingTopic(pairToken: String): List<RoomWalletConnectSession>


    @Query("SELECT * FROM $TABLE_NAME WHERE topic==:topic")
    fun getRoomListByTopicAsync(topic: String): Flow<List<RoomWalletConnectSession>>


    @Query("DELETE FROM $TABLE_NAME WHERE topic==:topic")
    fun deleteByTopic(topic: String)

    @Query("UPDATE $TABLE_NAME SET timeRequest = :timeRequest  WHERE topic==:topic")
    fun updateTimeRequestByTopic(topic: String, timeRequest: Long)


    fun insertOrUpdateEntity(entity: WalletConnectSession) = insertOrUpdate(entity.toRoom())

    fun insertOrUpdateEntity(entities: List<WalletConnectSession>) = insertOrUpdate(entities.map { it.toRoom() })


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(room: RoomWalletConnectSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rooms: List<RoomWalletConnectSession>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["topic"]
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class RoomWalletConnectSession(
    val topic: String,

    val extra: String = "",
    val walletId: String = "",

    val pairToken: String = "",
    val connectSource: String,

    val timeRequest: Long = System.currentTimeMillis(),
    val timeConnected: Long = System.currentTimeMillis()
)

private fun RoomWalletConnectSession.toEntity() = WalletConnectSession(topic, extra, walletId, pairToken, connectSource, timeRequest, timeConnected)

private fun WalletConnectSession.toRoom() = RoomWalletConnectSession(topic, extra, walletId, pairToken, connectSource, timeRequest, timeConnected)