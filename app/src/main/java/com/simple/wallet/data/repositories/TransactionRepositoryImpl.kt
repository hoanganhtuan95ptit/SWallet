package com.simple.wallet.data.repositories

import com.one.web3.Param
import com.one.web3.task.gaslimit.GasLimitTask
import com.one.web3.task.transaction.send.SendTransactionTask
import com.simple.state.ResultState
import com.simple.state.toFailed
import com.simple.task.executeSyncByPriority
import com.simple.wallet.data.task.transaction.gasprice.GasPriceTask
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Gas
import com.simple.wallet.domain.entities.Transaction
import com.simple.wallet.domain.repositories.TransactionRepository
import java.math.BigInteger

class TransactionRepositoryImpl(
    private val gasPriceTaskList: List<GasPriceTask>,
    private val gasLimitCallTaskList: List<GasLimitTask>,
    private val sendTransactionTaskList: List<SendTransactionTask>,
) : TransactionRepository {

    override suspend fun getGasList(chainId: Long, rpcUrls: List<String>, sync: Boolean): ResultState<List<Gas>> {

        return gasPriceTaskList.executeSyncByPriority(Param(chainId, rpcUrls, sync))
    }

    override suspend fun getGasLimit(transaction: Transaction, rpcUrls: List<String>, sync: Boolean): ResultState<BigInteger> {

        return let {

            org.web3j.protocol.core.methods.request.Transaction(
                transaction.from,
                BigInteger.ZERO,
                BigInteger.ZERO, BigInteger.ZERO,
                transaction.to,
                transaction.value,
                transaction.data
            )
        }.let {

            GasLimitTask.Param(
                it,
                chainId = transaction.chainId,
                rpcUrls = rpcUrls,
                sync = true
            )
        }.run {

            gasLimitCallTaskList.executeSyncByPriority(this)
        }
    }

    override suspend fun sendTransaction(transaction: Transaction, chain: Chain, rpcUrls: List<String>): ResultState<String> {

        val param = SendTransactionTask.Param(
            to = transaction.to,
            from = transaction.from,
            data = transaction.data,
            value = transaction.value,

            nonce = transaction.nonce,
            gasLimit = transaction.gasLimit,
            gasPriceWei = transaction.gasPriceWei.toBigInteger(),
            priorityFeeWei = transaction.priorityFeeWei.toBigInteger(),

            chainId = transaction.chainId,
            rpcUrls = rpcUrls,
            sync = true
        )


        val state = sendTransactionTaskList.executeSyncByPriority(param)


        return if (state is ResultState.Success) {

            ResultState.Success(state.data.first)
        } else {

            ResultState.Failed(state.toFailed()?.cause ?: RuntimeException(""))
        }
    }
}