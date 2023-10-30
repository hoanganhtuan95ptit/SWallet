package com.simple.wallet.domain.usecases.walletconnect

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletConnectRepository

class ApproveConnectUseCase(
    private val walletConnectRepository: WalletConnectRepository
) : BaseUseCase<ApproveConnectUseCase.Param, ResultState<Request>> {

    override suspend fun execute(param: Param?): ResultState<Request> {
        checkNotNull(param)

        return walletConnectRepository.approvalConnectAwait(param.wallet, param.request)
    }

    data class Param(val wallet: Wallet, val request: Request)
}