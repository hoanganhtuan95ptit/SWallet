package com.simple.wallet.data.socket

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.simple.analytics.logAnalytics
import com.simple.core.utils.extentions.resumeActive
import com.simple.core.utils.extentions.toJson
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.coreapp.utils.extentions.offerActiveAwait
import com.simple.crashlytics.logCrashlytics
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Chain.Companion.toNamespace
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.utils.exts.launchCollect
import com.simple.wallet.utils.exts.toPairingTopic
import com.simple.wallet.utils.exts.toSessionRequest
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.cacao.signature.SignatureType
import com.walletconnect.android.internal.common.exception.CannotFindSequenceForTopic
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.android.relay.NetworkClientTimeout
import com.walletconnect.android.utils.cacao.sign
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import com.walletconnect.web3.wallet.utils.CacaoSigner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import wallet.core.jni.PrivateKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class WalletConnectSocketImpl(
    private val application: Application,
) : WalletConnectSocket {

    private val scope = CoroutineScope(ProcessLifecycleOwner.get().lifecycleScope.coroutineContext + SupervisorJob() + Dispatchers.IO)


    private val sessionRequests = ConcurrentHashMap<Long, RequestParam>()

    private val sessionRequestIdAndCallbackMap = ConcurrentHashMap<Long, Runnable>()


    private val mutableSharedFlow: MutableSharedFlow<Wallet.Model> = MutableSharedFlow()


    private val initLazy by lazy {

        val start = System.currentTimeMillis()

        logAnalytics(TAG to "initialize")


        val relayUrl = "relay.walletconnect.com"

        val projectId = "af17b6e141446a916d5632f17e725bad"

        val serverUrl = "wss://$relayUrl?projectId=$projectId"

        val appMetaData = Core.Model.AppMetaData(
            name = "Krystal",
            description = "Simplest Web3 Wallet for Everyone",
            url = "https://krystal.app/",
            icons = listOf("https://krystal.app/assets/images/logos/krystal.svg"),
            redirect = "kotlin-web3wallet:/request"
        )

        val connectionType = ConnectionType.AUTOMATIC

        val networkClientTimeout = NetworkClientTimeout(
            timeout = 60 * 1000L,
            timeUnit = TimeUnit.MILLISECONDS
        )

        CoreClient.initialize(metaData = appMetaData, relayServerUrl = serverUrl, connectionType = connectionType, application = application, networkClientTimeout = networkClientTimeout, onError = { error ->

            logCrashlytics(java.lang.RuntimeException("$TAG CoreClient initialize", error.throwable))
        })


        Log.d("tuanha", "init: ${System.currentTimeMillis() - start}")

        val initParams = Wallet.Params.Init(core = CoreClient)

        Web3Wallet.initialize(initParams, onSuccess = {

            logAnalytics(TAG to "Web3Wallet initialize success")

            initCallbackLazy
        }, onError = { error ->

            logCrashlytics(java.lang.RuntimeException("$TAG Web3Wallet initialize", error.throwable))
        })


        Log.d("tuanha", "init1: ${System.currentTimeMillis() - start}")

    }

    private val initCallbackLazy by lazy {


        logAnalytics(TAG to "initCallback")


        Web3Wallet.setWalletDelegate(object : Web3Wallet.WalletDelegate {

            override fun onSessionProposal(sessionProposal: Wallet.Model.SessionProposal, verifyContext: Wallet.Model.VerifyContext) {

                Log.d("tuanha", "onSessionProposal: ")
                scope.launch { mutableSharedFlow.emit(sessionProposal) }
            }

            override fun onSessionRequest(sessionRequest: Wallet.Model.SessionRequest, verifyContext: Wallet.Model.VerifyContext) {

                Log.d("tuanha", "onSessionRequest: ")
                scope.launch { mutableSharedFlow.emit(sessionRequest) }
            }

            override fun onAuthRequest(authRequest: Wallet.Model.AuthRequest, verifyContext: Wallet.Model.VerifyContext) {

                Log.d("tuanha", "onAuthRequest: ")
                scope.launch { mutableSharedFlow.emit(authRequest) }
            }

            override fun onSessionDelete(sessionDelete: Wallet.Model.SessionDelete) {

                Log.d("tuanha", "onSessionDelete: ")
                scope.launch { mutableSharedFlow.emit(sessionDelete) }
            }

            override fun onSessionExtend(session: Wallet.Model.Session) {

                Log.d("tuanha", "onSessionExtend: ")
            }

            override fun onSessionSettleResponse(settleSessionResponse: Wallet.Model.SettledSessionResponse) {

                Log.d("tuanha", "onSessionSettleResponse: ")
                scope.launch { mutableSharedFlow.emit(settleSessionResponse) }
            }

            override fun onSessionUpdateResponse(sessionUpdateResponse: Wallet.Model.SessionUpdateResponse) {

                Log.d("tuanha", "onSessionUpdateResponse: ")
                scope.launch { mutableSharedFlow.emit(sessionUpdateResponse) }
            }

            override fun onConnectionStateChange(state: Wallet.Model.ConnectionState) {

                logAnalytics(TAG to "onConnectionStateChange", "data" to state.toJson())

                scope.launch { mutableSharedFlow.emit(state) }
            }

            override fun onError(error: Wallet.Model.Error) {

                logCrashlytics(RuntimeException("$TAG error: ", error.throwable))

                scope.launch { mutableSharedFlow.emit(error) }
            }
        })
    }


    private fun init(): Any {

        return initLazy
    }


    override fun onSessionDeleteAsync(sessionTopic: String) = channelFlow {
        init()

        mutableSharedFlow.filterIsInstance<Wallet.Model.SessionDelete.Success>().filter { event -> event.topic == sessionTopic }.launchCollect(this) { request ->

            logAnalytics(TAG to "onSessionDeleteAsync", "data" to request.toJson())

            offerActive(request)
        }

        awaitClose {
            logAnalytics(TAG to "onSessionDeleteAsync: awaitClose")
        }
    }

    override fun onSessionProposalAsync() = channelFlow<Request> {
        init()

//        Web3Wallet.getSessionProposals().toList().lastOrNull()?.let { request ->
//
//            logAnalytics(TAG to "onSessionProposalAsync", "type" to "getSessionProposals", "data" to request.toJson())
//
//            offerActive(request.toSessionRequest())
//        }

        mutableSharedFlow.filterIsInstance<Wallet.Model.SessionProposal>().launchCollect(this) { request ->

            logAnalytics(TAG to "onSessionProposalAsync", "type" to "launchCollect", "data" to request.toJson())

            offerActive(request.toSessionRequest())
        }

        awaitClose {
            logAnalytics(TAG to "onSessionProposalAsync: awaitClose")
        }
    }.map {

        delay(300)

        it
    }

    override fun onSessionRequestAsync() = channelFlow<Request> {
        init()

        val executionAwait: suspend () -> Unit = {

            suspendCancellableCoroutine { continuation ->

                val requestParam = sessionRequests.filter { it.value.request.method != Request.Method.ETH_CALL || it.value.request.method == Request.Method.SIGN_AUTH }.toList().first().second

                val request = requestParam.sessionRequest!!

                val sessionRequest = requestParam.request


                logAnalytics(TAG to "onSessionRequestAsync execution", "data" to request.toJson())


                sessionRequestIdAndCallbackMap[request.request.id] = Runnable {

                    sessionRequests.remove(sessionRequest.id)

                    continuation.resumeActive(Unit)
                }

                offerActive(sessionRequest)
            }
        }


        val mutex = Mutex()


        mutableSharedFlow.filterIsInstance<Wallet.Model.SessionRequest>().filter { event -> event.request.method != "eth_call" }.launchCollect(this) { request ->

            logAnalytics(TAG to "onSessionRequestAsync", "data" to request.toJson())

            val sessionRequest = request.toSessionRequest()
            sessionRequests[sessionRequest.id] = RequestParam(sessionRequest, sessionRequest = request)

            mutex.withLock {

                executionAwait()
            }
        }

        mutableSharedFlow.filterIsInstance<Wallet.Model.AuthRequest>().launchCollect(this) { authRequest ->

            logAnalytics(TAG to "onAuthRequestAsync", "data" to authRequest.toJson())

            val request = authRequest.toSessionRequest()
            sessionRequests[request.id] = RequestParam(request, authRequest = authRequest)

            mutex.withLock {

                executionAwait()
            }
        }

        launch {

            val a =
                "{\"chainId\":\"eip155:137\",\"peerMetaData\":{\"description\":\"Krystal wallet connect, powered by BlockNative\",\"icons\":[\"https://wallet.krystal.app/icon-192x192.png\",\"https://wallet.krystal.app/static/media/krystal.1dda4ba0.svg\"],\"name\":\"Krystal\",\"url\":\"https://wallet.krystal.app\"},\"request\":{\"id\":1698737314260042,\"method\":\"eth_sendTransaction\",\"params\":\"[{\\\"from\\\":\\\"0x7a2266331ac908931eef379650c9731cc60e5558\\\",\\\"to\\\":\\\"0x7a2266331ac908931eef379650c9731cc60e5558\\\",\\\"value\\\":\\\"0x9184e72a000\\\",\\\"data\\\":\\\"0x\\\",\\\"gasPrice\\\":null,\\\"maxFeePerGas\\\":null,\\\"maxPriorityFeePerGas\\\":null,\\\"gasLimit\\\":\\\"25200\\\",\\\"gas\\\":\\\"0x6270\\\"}]\"},\"topic\":\"e64b15ee919d3b2fc3b4f2da2989562df8988a4d69be29e62f2d9d2c474c97a4\"}"

//            mutableSharedFlow.emit(a.toObjectV2<Wallet.Model.SessionRequest>())
        }

        awaitClose {
            logAnalytics(TAG to "onSessionRequestAsync: awaitClose")
        }
    }

    override fun onEthCallRequestAsync() = channelFlow<List<Request>> {
        init()

        val executionAwait: suspend () -> Unit = {

            suspendCancellableCoroutine { continuation ->

                val idAndSessionRequestMap = sessionRequests.filter { it.value.request.method == Request.Method.ETH_CALL }.toMutableMap()

                val sessionRequestList = idAndSessionRequestMap.values.map { it.request }.toMutableList()

                sessionRequestList.forEach { sessionRequestQueue ->


                    logAnalytics(TAG to "onEthCallRequestAsync execution", "data" to sessionRequestQueue.toJson())


                    sessionRequestIdAndCallbackMap[sessionRequestQueue.id] = Runnable {

                        sessionRequests.remove(sessionRequestQueue.id)

                        val sessionRequestCache = idAndSessionRequestMap.remove(sessionRequestQueue.id)?.request

                        sessionRequestList.remove(sessionRequestCache)

                        if (sessionRequestList.isEmpty()) continuation.resumeActive(Unit)
                    }
                }

                offerActive(sessionRequestList)
            }
        }


        val mutex = Mutex()


        mutableSharedFlow.filterIsInstance<Wallet.Model.SessionRequest>().filter { event -> event.request.method == "eth_call" }.launchCollect(this) { request ->


            logAnalytics(TAG to "onEthCallRequestAsync", "data" to request.toJson())


            val sessionRequest = request.toSessionRequest()
            sessionRequests[sessionRequest.id] = RequestParam(sessionRequest, sessionRequest = request)

            mutex.withLock {

                executionAwait()
            }
        }


        awaitClose {
            logAnalytics(TAG to "onEthCallRequestAsync: awaitClose")
        }
    }


    override suspend fun pairAwait(pair: String) = channelFlow {
        init()


        val pairingTopic = pair.toPairingTopic()


        val jobTimeout = launch(start = CoroutineStart.LAZY) {

            delay(60 * 1000)

            logAnalytics(TAG to "pair: timeout")
            offerActive(ResultState.Failed())
        }


        mutableSharedFlow.filterIsInstance<Wallet.Model.AuthRequest>().filter { event -> pairingTopic.equals(event.pairingTopic, true) }.launchCollect(this) { request ->

            jobTimeout.cancel()

            logAnalytics(TAG to "pair: OnAuth Success")
            offerActive(ResultState.Failed(RuntimeException("")))
        }

        mutableSharedFlow.filterIsInstance<Wallet.Model.SessionProposal>().filter { event -> pairingTopic.equals(event.pairingTopic, true) }.launchCollect(this) { request ->

            jobTimeout.cancel()

            logAnalytics(TAG to "pair", "status" to "success", "data" to request.toJson())
            offerActive(ResultState.Success(request.toSessionRequest()))
        }


        val pairingParams = Wallet.Params.Pair(pair)

        logAnalytics(TAG to "pair: $pair")

        Web3Wallet.pair(pairingParams, onError = { error ->

            logCrashlytics(java.lang.RuntimeException("$TAG pair: ", error.throwable))
            offerActive(ResultState.Failed(error.throwable))
        }, onSuccess = {

            logAnalytics(TAG to "pair: pairingTopic:$pairingTopic")
            launch { jobTimeout.join() }
        })

        awaitClose {
            logAnalytics(TAG to "pair: awaitClose")
        }
    }.first()


    override suspend fun approvalConnectAwait(wallet: com.simple.wallet.domain.entities.Wallet, sessionProposal: Wallet.Model.SessionProposal) = channelFlow {
        init()


        val jobTimeout = launch {

            delay(60 * 1000)

            logAnalytics(TAG to "approvalConnectAwait: timeout")
            offerActive(ResultState.Failed(cause = TimeoutException("time out")))
        }


        mutableSharedFlow.filterIsInstance<Wallet.Model.SettledSessionResponse.Result>().launchCollect(this) { request ->

            jobTimeout.cancel()

            logAnalytics(TAG to "approvalConnectAwait", "status" to "success", "data" to request.toJson())
            offerActive(ResultState.Success(request))
        }

        mutableSharedFlow.filterIsInstance<Wallet.Model.SettledSessionResponse.Error>().launchCollect(this) { request ->

            jobTimeout.cancel()

            logCrashlytics(RuntimeException("$TAG approvalConnectAwait: onError ${request.errorMessage}"))
            offerActive(ResultState.Failed(RuntimeException(request.errorMessage)))
        }


        val namespaces: Map<String, Wallet.Model.Namespace.Session> = hashMapOf<String, Wallet.Model.Namespace.Session>().apply {

            putAll(generateNamespace(sessionProposal, wallet))
        }

        val approveProposal = Wallet.Params.SessionApprove(proposerPublicKey = sessionProposal.proposerPublicKey, namespaces = namespaces)

        logAnalytics(TAG to "approvalConnectAwait", "data" to approveProposal.toJson())

        Web3Wallet.approveSession(approveProposal, onError = { error ->

            logCrashlytics(java.lang.RuntimeException("$TAG approval: ", error.throwable))
            offerActive(ResultState.Failed(cause = error.throwable))
        }, onSuccess = {

            logAnalytics(TAG to "approvalConnectAwait")
            launch { jobTimeout.join() }
        })

        awaitClose {
            logAnalytics(TAG to "approvalConnectAwait: awaitClose")
        }
    }.first()

    override suspend fun rejectionConnectAwait(sessionProposal: Wallet.Model.SessionProposal) = channelFlow {
        init()


        val rejectionReason = "Reject Session"

        val reject = Wallet.Params.SessionReject(sessionProposal.proposerPublicKey, rejectionReason)

        logAnalytics(TAG to "rejectionConnectAwait", "data" to reject.toJson())

        Web3Wallet.rejectSession(reject, onError = { e ->

            logCrashlytics(java.lang.RuntimeException("$TAG rejection: ", e.throwable))
            offerActive(ResultState.Failed(cause = e.throwable))
        }, onSuccess = {

            logAnalytics(TAG to "rejectionConnectAwait")
            offerActive(ResultState.Success(true))
        })

        awaitClose {
            logAnalytics(TAG to "rejectionConnectAwait: awaitClose")
        }
    }.first()


    override suspend fun approvalAuthAwait(wallet: com.simple.wallet.domain.entities.Wallet, privateKey: PrivateKey, requestId: Long) = channelFlow {
        init()

        val request = sessionRequests.remove(requestId)?.authRequest ?: let {

            offerActiveAwait(ResultState.Failed(cause = RuntimeException("Not found request")))
            return@channelFlow
        }

        val issuer = "did:pkh:${request.payloadParams.chainId}:${wallet.addressMap.toList().first()}"

        val signature: Wallet.Model.Cacao.Signature = CacaoSigner.sign(
            Web3Wallet.formatMessage(Wallet.Params.FormatMessage(request.payloadParams, issuer)) ?: "",
            privateKey.data(),
            SignatureType.EIP191
        )

        val result = Wallet.Params.AuthRequestResponse.Result(request.id, signature, issuer)

        logAnalytics(TAG to "approvalAuthAwait", "data" to result.toJson())

        Web3Wallet.respondAuthRequest(result, onError = { error ->

            logCrashlytics(java.lang.RuntimeException("$TAG approvalAuth: ", error.throwable))
            offerActive(ResultState.Failed(cause = error.throwable))
        }, onSuccess = {

            logAnalytics(TAG to "approvalAuthAwait")
            offerActive(ResultState.Success(true))
        })

        awaitClose {
            logAnalytics(TAG to "approvalAuthAwait: awaitClose")
        }
    }.first()


    override suspend fun disconnectAwait(sessionTopic: String) = channelFlow {
        init()

        val disconnectParams = Wallet.Params.SessionDisconnect(sessionTopic)

        logAnalytics(TAG to "disconnectAwait", "data" to disconnectParams.toJson())

        Web3Wallet.disconnectSession(disconnectParams, onError = { e ->

            logCrashlytics(java.lang.RuntimeException("$TAG disconnectAwait: ", e.throwable))

            if (e.throwable is CannotFindSequenceForTopic) {

                offerActive(ResultState.Success(sessionTopic))
            } else {

                offerActive(ResultState.Failed(cause = e.throwable))
            }
        }, onSuccess = {

            logAnalytics(TAG to "disconnectAwait")
            offerActive(ResultState.Success(sessionTopic))
        })

        awaitClose {
            logAnalytics(TAG to "disconnectAwait: awaitClose")
        }
    }.first()


    override suspend fun approveRequestAwait(requestId: Long, result: String) = channelFlow {
        init()

        approveRequest(requestId, result, onError = { e ->

            offerActive(ResultState.Failed(cause = e))
        }, onSuccess = {
            offerActive(ResultState.Success(true))
        })

        awaitClose {

            logAnalytics(TAG to "approveRequestAwait: awaitClose")
        }
    }.first()

    override suspend fun rejectRequestAwait(requestId: Long, message: String) = channelFlow {
        init()

        rejectRequest(requestId, message, onError = { e ->

            offerActive(ResultState.Failed(cause = e))
        }, onSuccess = {

            offerActive(ResultState.Success(true))
        })

        awaitClose {

            logAnalytics(TAG to "rejectRequestAwait: awaitClose")
        }
    }.first()


    private fun approveRequest(requestId: Long, result: String, onError: (Throwable) -> Unit = {}, onSuccess: () -> Unit = {}) {
        init()

        val sessionRequest = sessionRequests.remove(requestId)?.sessionRequest ?: let {

            onError(RuntimeException("Not found request"))
            return
        }


        sessionRequestIdAndCallbackMap.remove(requestId)?.run()


        val jsonRpcResponse: Wallet.Model.JsonRpcResponse.JsonRpcResult = Wallet.Model.JsonRpcResponse.JsonRpcResult(requestId, result)

        val response = Wallet.Params.SessionRequestResponse(sessionTopic = sessionRequest.topic, jsonRpcResponse = jsonRpcResponse)

        logAnalytics(TAG to "approveRequest", "data" to response.toJson())

        Web3Wallet.respondSessionRequest(response, onError = { e ->

            logCrashlytics(java.lang.RuntimeException("$TAG approveRequest: ", e.throwable))
            onError(e.throwable)
        }, onSuccess = {

            logAnalytics(TAG to "approveRequest")
            onSuccess()
        })
    }

    private fun rejectRequest(requestId: Long, message: String = "", onError: (Throwable) -> Unit = {}, onSuccess: () -> Unit = {}) {
        init()

        val sessionRequest = sessionRequests.remove(requestId)?.sessionRequest ?: let {

            onError(RuntimeException("Not found request"))
            return
        }


        sessionRequestIdAndCallbackMap.remove(requestId)?.run()


        val jsonRpcResponse: Wallet.Model.JsonRpcResponse.JsonRpcError = Wallet.Model.JsonRpcResponse.JsonRpcError(requestId, code = 500, message.takeIf { it.isNotBlank() } ?: "reject")

        val response = Wallet.Params.SessionRequestResponse(sessionTopic = sessionRequest.topic, jsonRpcResponse = jsonRpcResponse)

        logAnalytics(TAG to "rejectRequest", "data" to response.toJson())

        Web3Wallet.respondSessionRequest(response, onError = { e ->

            logCrashlytics(RuntimeException("$TAG rejectRequest: ", e.throwable))
            onError(e.throwable)
        }, onSuccess = {

            logAnalytics(TAG to "rejectRequest")
            onSuccess()
        })
    }


    private fun generateNamespace(sessionProposal: Wallet.Model.SessionProposal, wallet: com.simple.wallet.domain.entities.Wallet): Map<String, Wallet.Model.Namespace.Session> = wallet.addressMap.map { entry ->

        val address = entry.key

        val namespace = entry.value.toNamespace()

        val chains = listOf(*sessionProposal.requiredNamespaces.getChains(namespace), *sessionProposal.optionalNamespaces.getChains(namespace))

        val accounts = chains.map { chain -> "$chain:${address}" }

        val events: List<String> = listOf(*sessionProposal.optionalNamespaces.getEvents(), *sessionProposal.requiredNamespaces.getEvents())

        val methods: List<String> = listOf(*sessionProposal.optionalNamespaces.getMethods(), *sessionProposal.requiredNamespaces.getMethods())

        namespace to Wallet.Model.Namespace.Session(chains, accounts, methods, events)
    }.toMap()


    private fun Map<String, Wallet.Model.Namespace.Proposal>.getChains(namespace: String) = flatMap { entry ->

        entry.value.chains?.filter { it.startsWith(namespace) } ?: emptyList()
    }.toTypedArray()


    private fun Map<String, Wallet.Model.Namespace.Proposal>.getEvents() = flatMap { entry ->

        entry.value.events
    }.toTypedArray()


    private fun Map<String, Wallet.Model.Namespace.Proposal>.getMethods() = flatMap { entry ->

        entry.value.methods
    }.toTypedArray()

    private data class RequestParam(
        val request: Request,

        val authRequest: Wallet.Model.AuthRequest? = null,
        val sessionRequest: Wallet.Model.SessionRequest? = null
    )

    companion object {

        private const val TAG = "WALLET_CONNECT"
    }
}