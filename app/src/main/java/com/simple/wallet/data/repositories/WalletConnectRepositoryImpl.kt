package com.simple.wallet.data.repositories

import com.simple.state.ResultState
import com.simple.state.doSuccess
import com.simple.wallet.data.dao.RoomWalletConnectSession
import com.simple.wallet.data.dao.WalletConnectSessionDao
import com.simple.wallet.data.socket.WalletConnectSocket
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Request.Slide.Companion.toSessionRequestSlideOrDefault
import com.simple.wallet.domain.repositories.WalletConnectRepository
import com.simple.wallet.utils.exts.sessionProposal
import com.walletconnect.web3.wallet.client.Wallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wallet.core.jni.PrivateKey
import java.util.concurrent.ConcurrentHashMap

class WalletConnectRepositoryImpl(
    private val walletConnectSocket: WalletConnectSocket,
    private val walletConnectSessionDao: WalletConnectSessionDao
) : WalletConnectRepository {


    private val pairingTopicAndSlideMap = ConcurrentHashMap<String, Request.Slide>()


    override fun onSessionDeleteAsync(sessionTopic: String): Flow<Wallet.Model.SessionDelete> {

        return walletConnectSocket.onSessionDeleteAsync(sessionTopic)
    }

    override fun onSessionProposalAsync(): Flow<Request> {

        return walletConnectSocket.onSessionProposalAsync().map {

            val sessionProposal = it.sessionProposal!!

            val slide = pairingTopicAndSlideMap[sessionProposal.pairingTopic]
                ?: walletConnectSessionDao.getListByPairingTopic(pairingTopic = sessionProposal.pairingTopic).firstOrNull()?.connectSource?.toSessionRequestSlideOrDefault()
                ?: return@map it

            it.slide = slide

            it
        }
    }


    override fun onSessionRequestAsync(): Flow<Request> {

        return walletConnectSocket.onSessionRequestAsync().map {

            val topic = it.topic ?: ""

            val slide = if (it.method == Request.Method.SIGN_AUTH) {

                walletConnectSessionDao.getListByPairingTopic(pairingTopic = topic)
            } else {

                walletConnectSessionDao.getListByTopic(topic = topic)
            }.run {

                first().connectSource.toSessionRequestSlideOrDefault()
            }

            it.slide = slide

            it
        }
    }

    override fun onEthCallRequestAsync(): Flow<Request> {

        return walletConnectSocket.onEthCallRequestAsync().map { it.first() }
    }


    override suspend fun pairAwait(pair: String, slide: Request.Slide): ResultState<Request> {

        val state = walletConnectSocket.pairAwait(pair)

        state.doSuccess {

            pairingTopicAndSlideMap[it.sessionProposal!!.pairingTopic] = slide
        }

        return state
    }


    override suspend fun approvalConnectAwait(wallet: com.simple.wallet.domain.entities.Wallet, request: Request): ResultState<Request> {


        val sessionProposal = request.sessionProposal!!


        val state = walletConnectSocket.approvalConnectAwait(wallet, sessionProposal)

        state.doSuccess {

            val slide = pairingTopicAndSlideMap[sessionProposal.pairingTopic]
                ?: walletConnectSessionDao.getListByPairingTopic(pairingTopic = sessionProposal.pairingTopic).firstOrNull()?.connectSource?.toSessionRequestSlideOrDefault()
                ?: return@doSuccess

            walletConnectSessionDao.insertOrUpdate(RoomWalletConnectSession(topic = it.session.topic, pairToken = sessionProposal.pairingTopic, connectSource = slide.value))
        }


        return if (state is ResultState.Failed) {

            state
        } else {

            ResultState.Success(request)
        }
    }

    override suspend fun rejectionConnectAwait(request: Request): ResultState<Request> {

        val sessionProposal = request.sessionProposal!!


        val state = walletConnectSocket.rejectionConnectAwait(sessionProposal)


        return if (state is ResultState.Failed) {

            state
        } else {

            ResultState.Success(request)
        }
    }


    override suspend fun approvalAuthAwait(wallet: com.simple.wallet.domain.entities.Wallet, privateKey: PrivateKey, requestId: Long): ResultState<Boolean> {

        return walletConnectSocket.approvalAuthAwait(wallet, privateKey, requestId)
    }


    override suspend fun disconnectAwait(sessionTopic: String): ResultState<String> {

        return walletConnectSocket.disconnectAwait(sessionTopic)
    }


    override suspend fun approveRequestAwait(requestId: Long, result: String): ResultState<Boolean> {

        return walletConnectSocket.approveRequestAwait(requestId, result)
    }

    override suspend fun rejectRequestAwait(requestId: Long, message: String): ResultState<Boolean> {

        return walletConnectSocket.rejectRequestAwait(requestId, message)
    }
}