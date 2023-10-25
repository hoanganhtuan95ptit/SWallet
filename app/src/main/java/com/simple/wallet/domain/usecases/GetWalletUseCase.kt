package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.state.toSuccess
import com.simple.task.executeAsyncAll
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository
import com.simple.wallet.domain.tasks.GenerateAddressTask
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class GetWalletUseCase(
    private val walletRepository: WalletRepository,
) : BaseUseCase<List<Wallet.Type>, List<Wallet>> {

    override suspend fun execute(param: List<Wallet.Type>?): List<Wallet> {

        val walletTypeList = param ?: Wallet.Type.values().toList()

        return walletRepository.getListWallet(walletTypeList)
    }
}