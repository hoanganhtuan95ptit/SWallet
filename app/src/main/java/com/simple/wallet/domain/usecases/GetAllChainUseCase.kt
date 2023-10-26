package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.repositories.ChainRepository


class GetAllChainUseCase(
    private val chainRepository: ChainRepository
) : BaseUseCase<GetAllChainUseCase.Param, List<Chain>> {

    override suspend fun execute(param: Param?): List<Chain> {
        checkNotNull(param)

        val list = arrayListOf<Chain>()

        if (param.isSupportAllChain) {

            list.add(Chain.ALL)
        }

        list.addAll(chainRepository.getListChain(Chain.Type.values()))

        return list
    }

    data class Param(val isSupportAllChain: Boolean)
}