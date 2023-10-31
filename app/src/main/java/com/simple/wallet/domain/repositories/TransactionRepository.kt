package com.simple.wallet.domain.repositories

import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Gas
import com.simple.wallet.domain.entities.Transaction
import java.math.BigInteger

interface TransactionRepository {

    suspend fun getGasList(chainId: Long, rpcUrls: List<String>, sync: Boolean): ResultState<List<Gas>>

    suspend fun getGasLimit(transaction: Transaction, rpcUrls: List<String>, sync: Boolean): ResultState<BigInteger>

    suspend fun sendTransaction(transaction: Transaction, chain: Chain, rpcUrls: List<String>): ResultState<String>
}