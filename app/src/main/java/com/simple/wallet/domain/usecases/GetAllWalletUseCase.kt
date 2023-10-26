package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository

class GetAllWalletUseCase(
    private val walletRepository: WalletRepository
) : BaseUseCase<GetAllWalletUseCase.Param, List<Wallet>> {

    override suspend fun execute(param: Param?): List<Wallet> {
        checkNotNull(param)

        val list = arrayListOf<Wallet>()

        if (param.isSupportAllWallet) {

            list.add(Wallet.ALL)
        }

        list.addAll(walletRepository.getListWallet(Wallet.Type.values().toList()))

        return list
    }

    data class Param(val isSupportAllWallet: Boolean)

}