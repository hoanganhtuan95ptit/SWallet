package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Token

interface TokenRepository {

    suspend fun sync()

    fun getTokenListBy(chainId: List<Long>, tokenType: List<Token.Type>): List<Token>

    fun getTokenListBy(chainId: List<Long>, tokenAddress: List<String>, tokenType: List<Token.Type>): List<Token>
}
