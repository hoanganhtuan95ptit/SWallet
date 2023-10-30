//package com.simple.wallet.data.dao
//
//import androidx.room.*
//import kotlinx.coroutines.flow.Flow
//
//private const val TABLE_NAME = "balances"
//
//@Dao
//interface BalanceTokenDao {
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE 1=1")
//    fun getRoomList(): List<RoomTokenBalance>
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE chainId = :chainId AND walletAddress COLLATE NOCASE = :walletAddress AND tokenAddress COLLATE NOCASE IN (:addressList)")
//    fun getRoomListByChainIdAndWalletAddressAndAddressList(chainId: Long, walletAddress: String, addressList: List<String>): List<RoomTokenBalance>
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainIds) AND walletAddress COLLATE NOCASE IN (:walletAddressList)")
//    fun getRoomListByChainIdListAndWalletAddressListAsync(chainIds: List<Long>, walletAddressList: List<String>): Flow<List<RoomTokenBalance>>
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainIds) AND walletAddress COLLATE NOCASE IN (:walletAddressList) AND balance NOT IN (:listBalanceWithout)")
//    fun getRoomListByChainIdListAndWalletAddressListAndWithoutBalanceAsync(chainIds: List<Long>, walletAddressList: List<String>, listBalanceWithout: List<String>): Flow<List<RoomTokenBalance>>
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE chainId IN (:chainIds) AND walletAddress COLLATE NOCASE IN (:walletAddress) AND tokenAddress COLLATE NOCASE IN (:addressList)")
//    fun getRoomListByChainIdListAndWalletAddressAndAddressList(chainIds: List<Long>, walletAddress: List<String>, addressList: List<String>): List<RoomTokenBalance>
//
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertOrUpdate(rooms: List<RoomTokenBalance>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertOrUpdate(room: RoomTokenBalance)
//
//    @Query("DELETE FROM $TABLE_NAME WHERE chainId = :chain AND walletAddress COLLATE NOCASE IN (:walletAddress)")
//    fun deleteBalanceForChain(chain: Long, walletAddress: List<String>)
//
//    @Query("DELETE FROM $TABLE_NAME WHERE chainId IN (:chainIdList) AND walletAddress COLLATE NOCASE IN (:walletAddressList)")
//    fun deleteByChainIdListAndWalletAddressList(chainIdList: List<Long>, walletAddressList: List<String>)
//
//    @Query("DELETE FROM $TABLE_NAME WHERE chainId = :chainId AND walletAddress COLLATE NOCASE = :walletAddress AND tokenAddress COLLATE NOCASE NOT IN (:listTokenAddress)")
//    fun deleteByWithoutListTokenAddress(chainId: Long, walletAddress: String, listTokenAddress: List<String>)
//}
//
//
//@Entity(
//    tableName = TABLE_NAME,
//    primaryKeys = ["chainId", "tokenAddress", "walletAddress"],
//)
//data class RoomTokenBalance(
//
//    val chainId: Long,
//    var tokenAddress: String = "",
//    var walletAddress: String = "",
//
//    var balance: String,
//)