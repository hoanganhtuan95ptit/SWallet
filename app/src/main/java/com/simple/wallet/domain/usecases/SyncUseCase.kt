package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.repositories.ChainRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class SyncUseCase(
    private val chainRepository: ChainRepository
) : BaseUseCase<Unit, Flow<Unit>> {

    override suspend fun execute(param: Unit?) = channelFlow<Unit>{

        launch {

            chainRepository.sync()
        }

        awaitClose {
        }
    }
}