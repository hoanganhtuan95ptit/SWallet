package com.simple.wallet.domain.usecases.wallet

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.state.toSuccess
import com.simple.task.executeAsyncAll
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository
import com.simple.wallet.domain.tasks.GenerateAddressTask
import kotlinx.coroutines.flow.first

class CreateWalletUseCase(
    private val walletRepository: WalletRepository,

    private val generateAddressTasks: List<GenerateAddressTask>
) : BaseUseCase<String, Wallet> {

    override suspend fun execute(param: String?): Wallet {
        checkNotNull(param)

        val mnemonic = walletRepository.generateMnemonic()


        val addressAndChainTypeState = generateAddressTasks.executeAsyncAll(GenerateAddressTask.Param(mnemonic, Wallet.Type.SEED_PHASE)).first()

        val addressAndChainType = addressAndChainTypeState.toSuccess()?.data?.filterIsInstance<ResultState.Success<Map<String, Chain.Type>>>()?.flatMap {

            it.data.toList()
        }?.toMap() ?: emptyMap()


        return walletRepository.importWallet(param, mnemonic, Wallet.Type.SEED_PHASE, addressAndChainType)
    }
}