package com.simple.wallet.data.repositories

import com.simple.wallet.data.dao.token.TokenDao
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.repositories.TokenRepository

class TokenRepositoryImpl(
    private val tokenDao: TokenDao
) : TokenRepository {

    override suspend fun sync() {
        TODO("Not yet implemented")
    }

    override fun getTokenListBy(chainId: List<Long>, tokenType: List<Token.Type>): List<Token> {

        return tokenDao.findListBy(chainId, tokenType)
    }

    override fun getTokenListBy(chainId: List<Long>, tokenAddress: List<String>, tokenType: List<Token.Type>): List<Token> {

        return tokenDao.findListBy(chainId, tokenAddress, tokenType)
    }
}