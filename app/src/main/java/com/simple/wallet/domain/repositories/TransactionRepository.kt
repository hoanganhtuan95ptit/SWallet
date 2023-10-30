package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Gas
import com.simple.wallet.domain.entities.Transaction
import java.math.BigInteger

interface TransactionRepository {

    fun getGasList(chainId: Long, rpcUrls: List<String>, sync: Boolean): List<Gas>

    fun getGasLimit(transaction: Transaction, rpcUrls: List<String>, sync: Boolean): BigInteger
}