package com.simple.wallet.data.task.chaintype.evm

import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.tasks.WalletTypeDetectTask
import com.simple.wallet.utils.exts.isEvmAddress
import com.simple.wallet.utils.exts.isEvmPrivateKey

class EvmWalletTypeDetectTask : WalletTypeDetectTask {

    override suspend fun executeTask(param: String): Wallet.Type {

        return if (param.isEvmPrivateKey()) {

            Wallet.Type.PRIVATE
        } else if (param.isEvmAddress()) {

            Wallet.Type.ADDRESS
        } else {

            error("not support")
        }
    }
}