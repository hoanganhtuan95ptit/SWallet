//package com.kyrd.transaction.data.task.decode
//
//import com.kyrd.base.utils.ext.asObject
//import com.kyrd.base.utils.ext.executeAsyncByFast
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
//import com.kyrd.transaction.data.task.node.l1fee.functionApproveEvmOf
//import com.kyrd.transaction.entities.Request
//import com.kyrd.transaction.entities.extra.ApproveExtra
//import com.kyrd.transaction.entities.TransactionType
//import com.one.web3.task.decimal.DecimalParam
//import com.one.web3.task.functionApproveEvmOf
//import com.simple.core.utils.extentions.asObject
//import com.simple.state.ResultState
//import com.simple.task.executeAsyncByFast
//import com.simple.wallet.domain.entities.extra.ApproveExtra
//import com.simple.wallet.domain.tasks.RequestDecodeTask
//import kotlinx.coroutines.flow.firstOrNull
//import org.web3j.abi.datatypes.Address
//import org.web3j.abi.datatypes.generated.Uint256
//import org.web3j.crypto.transaction.type.TransactionType
//import java.math.BigInteger
//
//class ApproveTransactionDecodeTask(
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
//        val transaction = param.transaction ?: error("not support function")
//
//        if (!transaction.data.startsWith("0x095ea7b3", true)) error("not support function")
//
//
//        val listRpc = urlChainDao.getRpcUrlForChain(param.chainId, 10)
//
//
//        val token = transaction.to.let { tokenAddress ->
//
//            val tokenDao = tokenDao.getListByChainIdAddressList(param.chainId, tokenAddress).firstOrNull()
//
//            val tokenLogo = configTokenDao.getRoomListByAsync(listOf(param.chainId), listOf(tokenAddress), TokenConfig.LOGO).firstOrNull()?.firstOrNull()?.value
//
//            Token(
//                address = tokenAddress,
//
//                logo = tokenLogo ?: "",
//
//                symbol = tokenDao?.symbol ?: symbolTask.executeAsyncByFast(SymbolParam(tokenAddress, param.chainId, listRpc)).toSuccess()!!.data,
//                decimals = tokenDao?.decimals ?: decimalTask.executeAsyncByFast(DecimalParam(tokenAddress, param.chainId, listRpc)).toSuccess()!!.data
//            )
//        }
//
//
//        val list = FunctionDecoder.decode(transaction.data, functionApproveEvmOf(Address.DEFAULT.value, BigInteger.ZERO))
//
//        val senderAddress = list.getOrNull(0).asObject<Address>().value
//
//        val amountApprove = list.getOrNull(1).asObject<Uint256>().value
//
//
//        transaction.apply {
//
//            type = TransactionType.APPROVAL
//
//            extra = ApproveExtra(
//                tokenApprove = token,
//                amountApprove = amountApprove,
//                senderAddress = senderAddress
//            )
//        }
//
//
//        return ResultState.Success(param)
//    }
//}