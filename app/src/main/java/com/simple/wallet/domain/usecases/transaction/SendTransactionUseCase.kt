//package com.simple.wallet.domain.usecases.transaction
//
//import com.kyrd.base.utils.ext.executeSyncByPriority
//import com.kyrd.chain.data.dao.ConfigChainDao
//import com.simple.wallet.data.dao.chain.RpcChainDao
//import com.kyrd.chain.entities.ChainConfigType
//import com.kyrd.transaction.data.task.node.send.SendParam
//import com.kyrd.transaction.data.task.node.send.SendTask
//import com.simple.coreapp.data.usecase.BaseUseCase
//import com.simple.state.ResultState
//import com.simple.task.executeSyncByPriority
//import java.math.BigDecimal
//import java.math.BigInteger
//
//class SendTransactionUseCase(
//    private val rpcChainDao: RpcChainDao,
//    private val configChainDao: ConfigChainDao,
//
//    private val sendTaskList: List<SendTask>
//) : BaseUseCase<SendTransactionUseCase.Param, ResultState<String>> {
//
//    override suspend fun execute(param: Param?): ResultState<String> {
//        checkNotNull(param)
//
//
//        val chainId = param.chainId
//
//        val rpcUrls = rpcChainDao.getRpcUrlForChain(chainId)
//
//        val isSupportEIP1559 = configChainDao.getListByChainIdAndTypes(chainId, ChainConfigType.IS_SUPPORT_EIP_1559).firstOrNull()?.value.toBoolean()
//
//
//        return SendParam(
//
//            to = param.to,
//            from = param.from,
//
//            data = param.data,
//
//            value = param.value,
//
//            nonce = param.nonce,
//            gasLimit = param.gasLimit,
//            gasPrice = param.gasPrice,
//            priorityFee = param.priorityFee,
//
//            isFromDApp = param.isFromDApp,
//            isSupportEIP1559 = isSupportEIP1559,
//
//            chainId = chainId,
//            rpcUrls = rpcUrls,
//            extraData = param.extraData
//        ).let {
//
//            sendTaskList.executeSyncByPriority(it)
//        }
//    }
//
//    data class Param(
//
//        val to: String,
//        val from: String,
//
//        val data: String,
//
//        val value: BigInteger,
//
//        open val nonce: Int,
//        open val gasLimit: BigInteger,
//        open val gasPrice: BigDecimal, // maxFee
//        open val priorityFee: BigDecimal,
//
//        open val isFromDApp: Boolean = false,
//
//        var chainId: Long,
//        var rpcUrls: List<String>,
//        var extraData: String = ""
//    ) : BaseUseCase.Param()
//}