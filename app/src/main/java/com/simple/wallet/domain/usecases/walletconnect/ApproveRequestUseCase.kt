package com.simple.wallet.domain.usecases.walletconnect

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.repositories.WalletConnectRepository

class ApproveRequestUseCase(
    private val walletConnectRepository: WalletConnectRepository
) : BaseUseCase<ApproveRequestUseCase.Param, ResultState<Boolean>> {

    override suspend fun execute(param: Param?): ResultState<Boolean> {
        checkNotNull(param)

        return walletConnectRepository.approveRequestAwait(param.requestId, param.message)
    }

    data class Param(val requestId: Long, val message: String)
}