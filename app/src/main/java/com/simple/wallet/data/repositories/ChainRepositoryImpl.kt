package com.simple.wallet.data.repositories

import com.simple.task.executeSyncByPriority
import com.simple.wallet.data.cache.AppCache
import com.simple.wallet.data.dao.chain.ChainDao
import com.simple.wallet.data.task.chain.ChainSyncTask
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.repositories.ChainRepository

class ChainRepositoryImpl(
    private val appCache: AppCache,
    private val chainDao: ChainDao,
    private val chainSyncTask: List<ChainSyncTask>
) : ChainRepository {

    override suspend fun sync() {

        chainSyncTask.executeSyncByPriority(Unit)
    }

    override fun getChainList(isSupportAllChain: Boolean, vararg value: Chain.Type): List<Chain> {

        val list = arrayListOf<Chain>()

        if (isSupportAllChain) {

            list.add(Chain.ALL)
        }

        list.addAll(chainDao.findListBy(*value))

        return list
    }

    override fun getChainBy(chainId: Long): Chain {

        return chainDao.findBy(chainId)
    }

    override fun getChainSelected(isSupportAllChain: Boolean): Chain {

        val chainId = if (isSupportAllChain) {
            appCache.getString(ALL_CHAIN_SELECTED)?.toLongOrNull() ?: Chain.ALL_NETWORK
        } else {
            appCache.getString(CHAIN_SELECTED)?.toLongOrNull() ?: Chain.ETHEREUM_ID
        }

        return if (chainId == Chain.ALL_NETWORK) {

            Chain.ALL
        } else {

            chainDao.findBy(chainId)
        }
    }

    override fun getRpcList(chainId: Long, limit: Int): List<String> {
        TODO("Not yet implemented")
    }
}

private val ALL_CHAIN_SELECTED by lazy {
    "ALL_CHAIN_SELECTED"
}

private val CHAIN_SELECTED by lazy {
    "CHAIN_SELECTED"
}
