package com.simple.wallet.domain.usecases.wallet

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository

class GetWalletByUseCase(
    private val walletRepository: WalletRepository
) : BaseUseCase<GetWalletByUseCase.Param, Wallet> {

    override suspend fun execute(param: Param?): Wallet {
        checkNotNull(param)

        return walletRepository.getWalletBy(param.walletAddress)
    }

    data class Param(val walletAddress: String)
}