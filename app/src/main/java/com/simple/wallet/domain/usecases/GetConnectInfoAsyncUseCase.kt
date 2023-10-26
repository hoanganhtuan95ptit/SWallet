package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.repositories.WalletConnectRepository
import com.simple.wallet.utils.exts.launchCollect
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetConnectInfoAsyncUseCase(
    private val walletConnectRepository: WalletConnectRepository
) : BaseUseCase<BaseUseCase.Param, Flow<Request>> {

    override suspend fun execute(param: BaseUseCase.Param?): Flow<Request> = channelFlow {

        walletConnectRepository.onSessionProposalAsync().launchCollect(this) {

            offerActive(it)
        }

        awaitClose { }
    }
}