package com.simple.wallet.domain.usecases.wallet

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository

class GetWalletSelectedUseCase(
    private val walletRepository: WalletRepository
) : BaseUseCase<GetWalletSelectedUseCase.Param, Wallet> {

    override suspend fun execute(param: Param?): Wallet {
        checkNotNull(param)

        return walletRepository.getWalletSelected(param.isSupportAllChain)
    }

    data class Param(val isSupportAllChain: Boolean)
}