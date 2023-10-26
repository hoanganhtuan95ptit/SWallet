//package com.kyrd.transaction.data.task.decode
//
//import com.kyrd.chain.data.dao.ConfigChainDao
//import com.kyrd.chain.data.dao.chain.token.ConfigTokenDao
//import com.kyrd.chain.data.dao.chain.token.TokenDao
//import com.kyrd.chain.entities.ChainConfigType
//import com.kyrd.krystal.domain.usecase.ResultState
//import com.kyrd.token.entities.Token
//import com.kyrd.token.entities.TokenConfig
//import com.kyrd.transaction.entities.Request
//import com.kyrd.transaction.entities.TransactionType
//import com.kyrd.transaction.entities.extra.TransferTransactionExtra
//import com.simple.wallet.domain.tasks.RequestDecodeTask
//import kotlinx.coroutines.flow.firstOrNull
//import org.web3j.abi.datatypes.Address
//import org.web3j.abi.datatypes.Function
//import org.web3j.abi.datatypes.generated.Uint256
//import java.math.BigInteger
//
//fun functionTransferEvmOf(receiverAddress: String, amount: BigInteger): Function {
//
//    val paramFirst = arrayOf(Address(receiverAddress), Uint256(amount))
//
//    return Function("transfer", listOfNotNull(*paramFirst), emptyList())
//}
//
//class TransferTransactionDecodeTask(
//    private val tokenDao: TokenDao,
//    private val configTokenDao: ConfigTokenDao,
//
//    private val configChainDao: ConfigChainDao,
//) : RequestDecodeTask {
//
//    override suspend fun executeTask(param: Request): ResultState<Request> {
//
//        val transaction = param.transaction ?: error("not support function")
//
//        if (!transaction.data.equals("0x", true)) error("not support function")
//
//
//        val token = configChainDao.getListByChainIdAndTypes(transaction.chainId, ChainConfigType.NATIVE_ADDRESS).firstOrNull()!!.value.let { tokenAddress ->
//
//            val tokenDao = tokenDao.getListByChainIdAddressList(transaction.chainId, tokenAddress).firstOrNull()!!
//
//            val tokenLogo = configTokenDao.getRoomListByAsync(listOf(transaction.chainId), listOf(tokenAddress), TokenConfig.LOGO).firstOrNull()?.firstOrNull()?.value
//
//            Token(
//                address = tokenAddress,
//
//                logo = tokenLogo ?: "",
//
//                symbol = tokenDao.symbol,
//                decimals = tokenDao.decimals
//            )
//        }
//
//        val receiverAddress = transaction.to
//
//        val amountTransfer = transaction.value
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