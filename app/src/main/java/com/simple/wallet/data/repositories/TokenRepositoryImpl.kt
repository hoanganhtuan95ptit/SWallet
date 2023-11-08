package com.simple.wallet.data.repositories

import com.simple.coreapp.utils.extentions.offerActive
import com.simple.state.doSuccess
import com.simple.task.executeSyncByPriority
import com.simple.wallet.data.dao.token.PriceTokenDao
import com.simple.wallet.data.dao.token.TokenDao
import com.simple.wallet.data.task.token.TokenPriceSyncTask
import com.simple.wallet.data.task.token.TokenSyncTask
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.repositories.TokenRepository
import com.simple.wallet.utils.exts.launchSchedule
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class TokenRepositoryImpl(
    private val tokenDao: TokenDao,
    private val priceTokenDao: PriceTokenDao,

    private val tokenSyncTaskList: List<TokenSyncTask>,
    private val tokenPriceSyncTaskList: List<TokenPriceSyncTask>
) : TokenRepository {

    override suspend fun syncToken() = channelFlow {

        launchSchedule {

            val tokenListState = tokenSyncTaskList.executeSyncByPriority(Unit)

            tokenListState.doSuccess { list ->

                offerActive(list)
            }

            24 * 60 * 60 * 1000L
        }


        awaitClose { }
    }

    override suspend fun syncPrice(list: List<Token>) = channelFlow {

        launchSchedule {

            val tokenPriceListState = tokenPriceSyncTaskList.executeSyncByPriority(list)

            tokenPriceListState.doSuccess { list ->

                offerActive(list)
            }

            24 * 60 * 60 * 1000L
        }

        awaitClose { }
    }

    override fun insertToken(list: List<Token>) {

        tokenDao.insert(list)
    }

    override fun insertPrice(list: List<Token.Price>) {

        priceTokenDao.insert(list)
    }


    override fun getTokenList(vararg tokenType: Token.Type): List<Token> {

        return tokenDao.findListBy(*tokenType)
    }

    override fun getTokenListAsync(vararg tokenType: Token.Type): Flow<List<Token>> {

        return tokenDao.findListByAsync(*tokenType)
    }


    override fun getTokenList(chainId: List<Long>, vararg tokenType: Token.Type): List<Token> {

        return tokenDao.findListBy(chainId, * tokenType)
    }

    override fun getTokenList(chainId: List<Long>, tokenAddress: List<String>, vararg tokenType: Token.Type): List<Token> {

        return tokenDao.findListBy(chainId, tokenAddress, *tokenType)
    }

    override fun getTokenPriceAsync(it: List<Token>): Flow<List<Token.Price>> {

        val chainIdAndTokenListMap = it.groupBy { it.chainId }

        val chainIdList = chainIdAndTokenListMap.keys.toList()

        val tokenAddressList = chainIdAndTokenListMap.values.flatMap { it }.map { it.address }

        return priceTokenDao.findListByAsync(chainIdList, tokenAddressList)
    }
}