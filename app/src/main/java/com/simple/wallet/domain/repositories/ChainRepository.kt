package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Chain
import kotlinx.coroutines.flow.Flow

interface ChainRepository {

    suspend fun sync()


    fun getChainList(isSupportAllChain: Boolean, vararg types: Chain.Type = Chain.Type.values()): List<Chain>

    fun getChainListAsync(isSupportAllChain: Boolean, vararg type: Chain.Type = Chain.Type.values()): Flow<List<Chain>>


    fun getChainBy(chainId: Long): Chain

    fun getChainSelected(isSupportAllChain: Boolean): Chain


    fun getRpcList(chainId: Long, limit: Int = 3): List<String>


    fun getSmartContractListAsync(vararg type: Chain.SmartContract.Type = Chain.SmartContract.Type.values()): Flow<List<Chain.SmartContract>>
}
