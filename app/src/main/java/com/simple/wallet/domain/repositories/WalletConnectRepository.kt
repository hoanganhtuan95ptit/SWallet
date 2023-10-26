package com.simple.wallet.domain.repositories

import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Request
import com.walletconnect.web3.wallet.client.Wallet
import kotlinx.coroutines.flow.Flow
import wallet.core.jni.PrivateKey

interface WalletConnectRepository {


    fun onSessionDeleteAsync(sessionTopic: String): Flow<Wallet.Model.SessionDelete>

    fun onSessionProposalAsync(): Flow<Request>


    fun onEthCallRequestAsync(): Flow<Request>

    fun onSessionRequestAsync(): Flow<Request>


    suspend fun pairAwait(pair: String, slide: Request.Slide): ResultState<Request>


    suspend fun approvalConnectAwait(wallet: com.simple.wallet.domain.entities.Wallet, request: Request): ResultState<Request>

    suspend fun rejectionConnectAwait(request: Request): ResultState<Request>


    suspend fun approvalAuthAwait(wallet: com.simple.wallet.domain.entities.Wallet, privateKey: PrivateKey, requestId: Long): ResultState<Boolean>


    suspend fun disconnectAwait(sessionTopic: String): ResultState<String>


    suspend fun approveRequestAwait(requestId: Long, result: String): ResultState<Boolean>

    suspend fun rejectRequestAwait(requestId: Long, message: String): ResultState<Boolean>
}