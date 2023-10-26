//package com.simple.wallet.data.task.decode
//
//import com.kyrd.base.utils.ext.executeAsyncByFast
//import com.kyrd.base.utils.ext.toTree
//import com.kyrd.chain.data.dao.UrlChainDao
//import com.kyrd.chain.data.dao.chain.token.ConfigTokenDao
//import com.kyrd.chain.data.dao.chain.token.TokenDao
//import com.kyrd.chain.data.task.node.decimal.DecimalParam
//import com.kyrd.chain.data.task.node.decimal.DecimalTask
//import com.kyrd.chain.data.task.node.symbol.SymbolParam
//import com.kyrd.chain.data.task.node.symbol.SymbolTask
//import com.kyrd.krystal.domain.usecase.ResultState
//import com.kyrd.krystal.domain.usecase.toSuccess
//import com.kyrd.token.entities.Token
//import com.kyrd.token.entities.TokenConfig
//import com.kyrd.transaction.entities.MessageType
//import com.kyrd.transaction.entities.Request
//import com.kyrd.transaction.entities.extra.ApproveExtra
//import com.one.web3.task.decimal.DecimalParam
//import com.simple.state.ResultState
//import com.simple.task.executeAsyncByFast
//import com.simple.wallet.domain.entities.extra.ApproveExtra
//import com.simple.wallet.domain.tasks.RequestDecodeTask
//import kotlinx.coroutines.flow.firstOrNull
//
//class PermitMessageDecodeTask(
//    private val tokenDao: TokenDao,
//    private val configTokenDao: ConfigTokenDao,
//
//    private val urlChainDao: UrlChainDao,
//
//    private val symbolTask: List<SymbolTask>,
//    private val decimalTask: List<DecimalTask>
//) : RequestDecodeTask {
//
//    override suspend fun executeTask(param: Request): ResultState<Request> {
//
//        val message = param.message ?: error("not support function")
//
//
//        val data by lazy {
//            message.message.toTree()
//        }
//
//        if (message.type != MessageType.SIGN_MESSAGE_TYPED && data.get("primaryType").textValue().equals("Permit", true)) {
//
//            error("not support function")
//        }
//
//
//        val listRpc = urlChainDao.getRpcUrlForChain(message.chainId, 10)
//
//
//        val tokenApprove = data.get("domain").get("verifyingContract").textValue().let { tokenAddress ->
//
//            val tokenDao = tokenDao.getListByChainIdAddressList(message.chainId, tokenAddress).firstOrNull()
//
//            val tokenLogo = configTokenDao.getRoomListByAsync(listOf(message.chainId), listOf(tokenAddress), TokenConfig.LOGO).firstOrNull()?.firstOrNull()?.value
//
//            Token(
//                address = tokenAddress,
//
//                logo = tokenLogo ?: "",
//
//                symbol = tokenDao?.symbol ?: symbolTask.executeAsyncByFast(SymbolParam(tokenAddress, message.chainId, listRpc)).toSuccess()!!.data,
//                decimals = tokenDao?.decimals ?: decimalTask.executeAsyncByFast(DecimalParam(tokenAddress, message.chainId, listRpc)).toSuccess()!!.data
//            )
//        }
//
//
//        val amountApprove = data.get("message").get("value").textValue().toBigInteger()
//
//        val senderAddress = data.get("message").get("spender").textValue()
//
//
//        message.apply {
//
//            type = MessageType.SIGN_PERMIT
//
//            extra = ApproveExtra(
//                tokenApprove = tokenApprove,
//                amountApprove = amountApprove,
//
//                senderAddress = senderAddress
//            )
//        }
//
//
//        return ResultState.Success(param)
//    }
//}