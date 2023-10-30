package com.simple.wallet.domain.usecases.wallet

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository

class GetAllWalletUseCase(
    private val walletRepository: WalletRepository
) : BaseUseCase<GetAllWalletUseCase.Param, List<Wallet>> {

    override suspend fun execute(param: Param?): List<Wallet> {
        checkNotNull(param)

        return walletRepository.getListWallet(param.isSupportAllWallet, Wallet.Type.values().toList())
    }

    data class Param(val isSupportAllWallet: Boolean)

}