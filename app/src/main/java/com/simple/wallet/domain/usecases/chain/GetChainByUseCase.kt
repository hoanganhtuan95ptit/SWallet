package com.simple.wallet.domain.usecases.chain

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.repositories.ChainRepository

class GetChainByUseCase(
    private val chainRepository: ChainRepository
) : BaseUseCase<GetChainByUseCase.Param, List<Chain>> {

    override suspend fun execute(param: Param?): List<Chain> {
        checkNotNull(param)

        return chainRepository.getChainBy(param.chainId).let { listOf(it) }
    }

    data class Param(val chainId: Long)
}