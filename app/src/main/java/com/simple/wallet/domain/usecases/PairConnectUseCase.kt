package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.repositories.WalletConnectRepository

class PairConnectUseCase(
    private val walletConnectRepository: WalletConnectRepository
) : BaseUseCase<PairConnectUseCase.Param, ResultState<Request>> {

    override suspend fun execute(param: Param?): ResultState<Request> {
        checkNotNull(param)

        return walletConnectRepository.pairAwait(param.pair, param.from)
    }

    data class Param(val pair: String, val from: Request.Slide = Request.Slide.ANOTHER_DEVICE)
}