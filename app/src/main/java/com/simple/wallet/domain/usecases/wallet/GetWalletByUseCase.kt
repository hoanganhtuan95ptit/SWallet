package com.simple.wallet.domain.usecases.wallet

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository

class GetWalletByUseCase(
    private val walletRepository: WalletRepository
) : BaseUseCase<GetWalletByUseCase.Param, List<Wallet>> {

    override suspend fun execute(param: Param?): List<Wallet> {
        checkNotNull(param)

        return walletRepository.getWalletBy(param.walletAddress).let {
            listOf(it)
        }
    }

    data class Param(val walletAddress: String)
}