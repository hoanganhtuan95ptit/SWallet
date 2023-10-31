package com.simple.wallet.domain.usecases.transaction

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.state.ResultState
import com.simple.state.toFailed
import com.simple.wallet.GAS_LIMIT_DEFAULT
import com.simple.wallet.domain.entities.Transaction
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.TransactionRepository
import com.simple.wallet.utils.exts.launchSchedule
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.math.BigInteger

class GetGasLimitAsyncUseCase(
    private val chainRepository: ChainRepository,
    private val transactionRepository: TransactionRepository,
) : BaseUseCase<GetGasLimitAsyncUseCase.Param, Flow<ResultState<Pair<BigInteger, Pair<BigInteger, BigInteger>>>>> {

    override suspend fun execute(param: Param?): Flow<ResultState<Pair<BigInteger, Pair<BigInteger, BigInteger>>>> = channelFlow {
        checkNotNull(param)

        val chainId = param.transaction.chainId

        val rpcUrls = chainRepository.getRpcList(chainId)

        launchSchedule {

            kotlin.runCatching {

                val gasLimitState = transactionRepository.getGasLimit(param.transaction, rpcUrls = rpcUrls, false)

                if (gasLimitState !is ResultState.Success) error(gasLimitState.toFailed()?.cause?.message ?: "")

                val gasLimit = gasLimitState.data
//                val gasLimitCommon =
//                    gasLimitCallTaskList.execute(GasLimitTransferParam(tokenAmount = BigInteger.ONE, isNativeToken = true, walletAddress = param.transaction.from, receiverAddress = param.transaction.from, chainId = chainId, rpcUrls = rpcUrls))

                offerActive(ResultState.Success(Pair(gasLimit, Pair(BigInteger.ZERO, GAS_LIMIT_DEFAULT))))
            }.getOrElse {

                offerActive(ResultState.Failed(it))
            }

            15 * 1000L
        }

        awaitClose()
    }

    data class Param(val transaction: Transaction) : BaseUseCase.Param()
}