package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.TokenRepository
import com.simple.wallet.domain.repositories.UrlRepository
import com.simple.wallet.utils.exts.launchCollect
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SyncUseCase(
    private val urlRepository: UrlRepository,
    private val chainRepository: ChainRepository,

    private val tokenRepository: TokenRepository,
) : BaseUseCase<Unit, Flow<Unit>> {

    override suspend fun execute(param: Unit?) = channelFlow<Unit> {

        launch {

            urlRepository.sync()
        }

        launch {

            chainRepository.sync()
        }

        tokenRepository.getTokenListAsync().flatMapLatest {

            tokenRepository.syncPrice(it)
        }.map {

            tokenRepository.insertPrice(it)
        }.launchCollect(this){

        }

        tokenRepository.syncToken().map {

            tokenRepository.insertToken(it)
        }.launchCollect(this){

        }

        awaitClose {
        }
    }
}