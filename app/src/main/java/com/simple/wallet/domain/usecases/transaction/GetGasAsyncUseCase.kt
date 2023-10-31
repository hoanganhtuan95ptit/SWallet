package com.simple.wallet.domain.usecases.transaction

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.coreapp.utils.Lock
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Gas
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.TransactionRepository
import com.simple.wallet.utils.exts.launchSchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class GetGasAsyncUseCase(
    private val chainRepository: ChainRepository,
    private val transactionRepository: TransactionRepository,
) : BaseUseCase<GetGasAsyncUseCase.Param, Flow<List<Gas>>> {


    private val chainIdAndGasList = ConcurrentHashMap<Long, Pair<Long, List<Gas>>>()


    override suspend fun execute(param: Param?): Flow<List<Gas>> = channelFlow {
        checkNotNull(param)

        val rpcUrls = chainRepository.getRpcList(param.chainId, 3)

        launchScheduleWithLock(param.chainId) {

            val pair = chainIdAndGasList[param.chainId] ?: Pair(0L, emptyList())

            val gasList = pair.second

            val timeCache = pair.first

            val hasCache = timeCache != 0L


            if (System.currentTimeMillis() - timeCache < 15 * 1000) {

                offerActive(gasList)
                return@launchScheduleWithLock 5 * 1000L
            }


            val gasListState = transactionRepository.getGasList(param.chainId, rpcUrls, true)

            if (gasListState !is ResultState.Success) {

                offerActive(gasList)
                return@launchScheduleWithLock 5 * 1000L
            }


            val list = gasListState.data

            if (list.isEmpty() || list.all { it.gasPriceWei in listOf(BigDecimal.ZERO) }) {

                if (hasCache) offerActive(gasList)
                return@launchScheduleWithLock if (hasCache) 5 * 1000L else 500L
            }

            offerActive(list)

            chainIdAndGasList[param.chainId] = Pair(System.currentTimeMillis(), list)

            15 * 1000L
        }

        awaitClose {

            Lock.cancel(param.chainId)
        }
    }.distinctUntilChangedBy {

        it.map { gas -> gas.gasPriceWei }
    }

    private fun CoroutineScope.launchScheduleWithLock(owner: Any, block: suspend CoroutineScope.() -> Long) = launchSchedule {

        Lock.withLock(owner) {

            block.invoke(this)
        }
    }

    class Param(val chainId: Long) : BaseUseCase.Param()
}
