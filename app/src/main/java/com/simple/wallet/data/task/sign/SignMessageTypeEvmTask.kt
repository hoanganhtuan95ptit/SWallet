//package com.simple.wallet.data.task.sign
//
//import com.kyrd.transaction.entities.Request
//import com.kyrd.wallet.domain.WalletRepository
//import com.kyrd.wallet.utils.ext.toHexString
//import com.simple.state.ResultState
//import com.simple.wallet.domain.entities.Request
//import com.simple.wallet.domain.repositories.WalletRepository
//import com.simple.wallet.domain.tasks.SignParam
//import com.simple.wallet.domain.tasks.SignTask
//import com.simple.wallet.utils.exts.toHex
//import org.web3j.crypto.Credentials
//import org.web3j.crypto.Sign
//
//class SignMessageTypeEvmTask(
//    val walletRepository: WalletRepository
//) : SignTask {
//
//    override suspend fun executeTask(param: SignParam): String {
//
//        val sessionMessage = param.request
//
//        val method = sessionMessage.method
//
//        val message = sessionMessage.message?.message
//
//        val walletAddress = sessionMessage.walletAddress
//
//        if (message == null || walletAddress == null || method != Request.Method.SIGN_MESSAGE_TYPED) {
//
//            error("not support")
//        }
//
//        val privateKey = walletRepository.getPrivateKey(walletAddress)
//
//        val credentials = Credentials.create(privateKey.data().toHexString(false))
//
//        return Sign.signTypedData(message, credentials.ecKeyPair).let {
//
//            it.r.plus(it.s).plus(it.v).toHex()
//        }
//    }
//}