package com.simple.wallet.domain.usecases.token

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.repositories.TokenRepository

class GetTokenByUseCase(
    private val tokenRepository: TokenRepository
) : BaseUseCase<GetTokenByUseCase.Param, List<Token>> {

    override suspend fun execute(param: Param?): List<Token> {
        checkNotNull(param)

        return if (param.chainId.isNotEmpty() && param.tokenType.isNotEmpty()) {

            tokenRepository.getTokenList(param.chainId, *param.tokenType.toTypedArray())
        } else {

            tokenRepository.getTokenList(param.chainId, param.tokenAddress, *param.tokenType.toTypedArray())
        }
    }

    data class Param(val chainId: List<Long>, val tokenAddress: List<String> = emptyList(), val tokenType: List<Token.Type> = Token.Type.values().toList())
}