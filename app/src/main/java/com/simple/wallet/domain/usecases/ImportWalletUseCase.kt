package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.state.toSuccess
import com.simple.task.executeAsyncAll
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository
import com.simple.wallet.domain.tasks.GenerateAddressTask
import kotlinx.coroutines.flow.first

class ImportWalletUseCase(
    private val walletRepository: WalletRepository,

    private val generateAddressTasks: List<GenerateAddressTask>
) : BaseUseCase<ImportWalletUseCase.Param, Wallet> {

    override suspend fun execute(param: Param?): Wallet {
        checkNotNull(param)

        val addressAndChainTypeState = generateAddressTasks.executeAsyncAll(GenerateAddressTask.Param(param.key, param.type)).first()

        val addressAndChainType = addressAndChainTypeState.toSuccess()?.data?.filterIsInstance<ResultState.Success<Map<String, Chain.Type>>>()?.flatMap {

            it.data.toList()
        }?.toMap() ?: emptyMap()

        return walletRepository.importWallet(param.name, param.key, param.type, addressAndChainType)
    }

    data class Param(val name: String, val key: String, val type: Wallet.Type) : BaseUseCase.Param()
}