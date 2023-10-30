package com.simple.wallet.domain.usecases.walletconnect

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.repositories.WalletConnectRepository

class RejectConnectUseCase(
    private val walletConnectRepository: WalletConnectRepository
) : BaseUseCase<RejectConnectUseCase.Param, ResultState<Request>> {

    override suspend fun execute(param: Param?): ResultState<Request> {
        checkNotNull(param)

        return walletConnectRepository.rejectionConnectAwait(param.request)
    }

    data class Param(val request: Request)
}