package com.simple.wallet.domain.usecases.transaction

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Transaction
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.TransactionRepository

class SendTransactionUseCase(
    private val chainRepository: ChainRepository,
    private val transactionRepository: TransactionRepository,
) : BaseUseCase<SendTransactionUseCase.Param, ResultState<String>> {

    override suspend fun execute(param: Param?): ResultState<String> {
        checkNotNull(param)

        val chain = chainRepository.getChainBy(param.transaction.chainId)

        val rpcUrls = chainRepository.getRpcList(param.transaction.chainId)

        return transactionRepository.sendTransaction(param.transaction, chain, rpcUrls)
    }

    data class Param(val transaction: Transaction) : BaseUseCase.Param()
}