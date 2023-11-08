package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Balance
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Wallet
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {

    fun getBalanceListAsync(walletAddressList: List<String>): Flow<List<Balance>>

    fun getBalanceListAsync(chainIdList: List<Long>, walletAddressList: List<String>): Flow<List<Balance>>


    suspend fun getBalanceList(chain: Chain, rpcList: List<String>, walletList: List<Wallet>, tokenList: List<Token>): List<Balance>

    suspend fun getBalanceList(chain: Chain, rpcList: List<String>, smartContractList: List<Chain.SmartContract>, walletList: List<Wallet>, tokenList: List<Token>): List<Balance>


    fun insert(vararg balance: Balance)
}