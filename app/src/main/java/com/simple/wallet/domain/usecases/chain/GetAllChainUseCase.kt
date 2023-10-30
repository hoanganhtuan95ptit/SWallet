package com.simple.wallet.domain.usecases.chain

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.repositories.ChainRepository


class GetAllChainUseCase(
    private val chainRepository: ChainRepository
) : BaseUseCase<GetAllChainUseCase.Param, List<Chain>> {

    override suspend fun execute(param: Param?): List<Chain> {
        checkNotNull(param)

        return chainRepository.getChainList(param.isSupportAllChain, *Chain.Type.values())
    }

    data class Param(val isSupportAllChain: Boolean)
}