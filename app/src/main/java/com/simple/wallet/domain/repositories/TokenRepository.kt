package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Token
import kotlinx.coroutines.flow.Flow

interface TokenRepository {

    suspend fun syncToken(): Flow<List<Token>>

    suspend fun syncPrice(list: List<Token>): Flow<List<Token.Price>>


    fun insertToken(list: List<Token>)

    fun insertPrice(list: List<Token.Price>)


    fun getTokenList(vararg tokenType: Token.Type = Token.Type.values()): List<Token>

    fun getTokenListAsync(vararg tokenType: Token.Type = Token.Type.values()): Flow<List<Token>>

    fun getTokenList(chainId: List<Long>, vararg tokenType: Token.Type = Token.Type.values()): List<Token>

    fun getTokenList(chainId: List<Long>, tokenAddress: List<String>, vararg tokenType: Token.Type = Token.Type.values()): List<Token>


    fun getTokenPriceAsync(it: List<Token>): Flow<List<Token.Price>>
}
