package com.simple.wallet.domain.usecases.chain

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.repositories.ChainRepository

class GetChainSelectedUseCase(
    private val chainRepository: ChainRepository
) : BaseUseCase<GetChainSelectedUseCase.Param, Chain> {

    override suspend fun execute(param: Param?): Chain {
        checkNotNull(param)

        return chainRepository.getChainSelected(param.isSupportAllChain)
    }

    data class Param(val isSupportAllChain: Boolean)
}