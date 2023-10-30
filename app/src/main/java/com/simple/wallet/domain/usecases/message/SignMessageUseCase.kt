package com.simple.wallet.domain.usecases.message

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.repositories.WalletRepository

class SignMessageUseCase(
    private val walletRepository: WalletRepository
) : BaseUseCase<SignMessageUseCase.Param, ResultState<String>> {

    override suspend fun execute(param: Param?): ResultState<String> {
        checkNotNull(param)

        return walletRepository.signMessage(param.request)
    }

    data class Param(
        val request: Request
    ) : BaseUseCase.Param()
}