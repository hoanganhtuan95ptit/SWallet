package com.simple.wallet.data.task.chaintype

import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.tasks.WalletTypeDetectTask
import com.simple.wallet.utils.exts.isEvmAddress
import com.simple.wallet.utils.exts.isEvmPrivateKey
import com.simple.wallet.utils.exts.isSeedPhrase

class SeedPhraseWalletTypeDetectTask : WalletTypeDetectTask {

    override suspend fun executeTask(param: String): Wallet.Type {

        return if (param.isSeedPhrase()) {

            Wallet.Type.SEED_PHASE
        } else {

            error("not support")
        }
    }
}