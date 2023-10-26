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
//import com.kyrd.transaction.entities.Request
//import com.kyrd.transaction.entities.TransactionType
//import com.kyrd.transaction.entities.extra.TransferTransactionExtra
//import com.simple.wallet.domain.tasks.RequestDecodeTask
//import kotlinx.coroutines.flow.firstOrNull
//import org.web3j.abi.datatypes.Address
//import org.web3j.abi.datatypes.generated.Uint256
//import java.math.BigInteger
//
//class TransferErc20TransactionDecodeTask(
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
//        if (!transaction.data.startsWith("0xa9059cbb", true)) error("not support function")
//
//
//        val listRpc = urlChainDao.getRpcUrlForChain(transaction.chainId, 10)
//
//
//        val token = transaction.to.let { tokenAddress ->
//
//            val tokenDao = tokenDao.getListByChainIdAddressList(transaction.chainId, tokenAddress).firstOrNull()
//
//            val tokenLogo = configTokenDao.getRoomListByAsync(listOf(transaction.chainId), listOf(tokenAddress), TokenConfig.LOGO).firstOrNull()?.firstOrNull()?.value
//
//            Token(
//                address = tokenAddress,
//
//                logo = tokenLogo ?: "",
//
//                symbol = tokenDao?.symbol ?: symbolTask.executeAsyncByFast(SymbolParam(tokenAddress, transaction.chainId, listRpc)).toSuccess()!!.data,
//                decimals = tokenDao?.decimals ?: decimalTask.executeAsyncByFast(DecimalParam(tokenAddress, transaction.chainId, listRpc)).toSuccess()!!.data
//            )
//        }
//
//
//        val list = FunctionDecoder.decode(transaction.data, functionTransferEvmOf(Address.DEFAULT.value, BigInteger.ZERO))
//
//        val receiverAddress = list.getOrNull(0).asObject<Address>().value
//
//        val amountTransfer = list.getOrNull(1).asObject<Uint256>().value
//
//
//        transaction.apply {
//
//            type = TransactionType.SEND
//
//            extra = TransferTransactionExtra(
//                tokenTransfer = token,
//                amountTransfer = amountTransfer,
//
//                receiverAddress = receiverAddress
//            )
//        }
//
//        return ResultState.Success(param)
//    }
//}
