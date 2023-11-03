package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.UrlRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class SyncUseCase(
    private val urlRepository: UrlRepository,
    private val chainRepository: ChainRepository
) : BaseUseCase<Unit, Flow<Unit>> {

    override suspend fun execute(param: Unit?) = channelFlow<Unit> {

        launch {

            urlRepository.sync()
        }

        launch {

            chainRepository.sync()
        }

        awaitClose {
        }
    }
}