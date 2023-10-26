package com.simple.wallet.data.socket

import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Request
import com.walletconnect.web3.wallet.client.Wallet
import kotlinx.coroutines.flow.Flow
import wallet.core.jni.PrivateKey

interface WalletConnectSocket {


    fun onSessionDeleteAsync(sessionTopic: String): Flow<Wallet.Model.SessionDelete>

    fun onSessionProposalAsync(): Flow<Request>


    fun onSessionRequestAsync(): Flow<Request>

    fun onEthCallRequestAsync(): Flow<List<Request>>


    suspend fun pairAwait(pair: String): ResultState<Request>


    suspend fun approvalConnectAwait(wallet: com.simple.wallet.domain.entities.Wallet, sessionProposal: Wallet.Model.SessionProposal): ResultState<Wallet.Model.SettledSessionResponse.Result>

    suspend fun rejectionConnectAwait(sessionProposal: Wallet.Model.SessionProposal): ResultState<Boolean>


    suspend fun approvalAuthAwait(wallet: com.simple.wallet.domain.entities.Wallet, privateKey: PrivateKey, requestId: Long): ResultState<Boolean>


    suspend fun disconnectAwait(sessionTopic: String): ResultState<String>


    suspend fun approveRequestAwait(requestId: Long, result: String): ResultState<Boolean>

    suspend fun rejectRequestAwait(requestId: Long, message: String): ResultState<Boolean>
}