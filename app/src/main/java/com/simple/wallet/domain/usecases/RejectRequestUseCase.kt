package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.repositories.WalletConnectRepository

class RejectRequestUseCase(
    private val walletConnectRepository: WalletConnectRepository
) : BaseUseCase<RejectRequestUseCase.Param, ResultState<Boolean>> {

    override suspend fun execute(param: Param?): ResultState<Boolean> {
        checkNotNull(param)

        return walletConnectRepository.rejectRequestAwait(param.requestId, param.message)
    }

    data class Param(val requestId: Long, val message: String)
}