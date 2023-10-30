package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Chain

interface ChainRepository {

    suspend fun sync()


    fun getChainList(isSupportAllChain: Boolean, vararg value: Chain.Type): List<Chain>


    fun getChainBy(chainId: Long): Chain

    fun getChainSelected(isSupportAllChain: Boolean): Chain


    fun getRpcList(chainId: Long, limit: Int = 3): List<String>
}
