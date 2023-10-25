package com.simple.wallet.data.task.chaintype.evm

import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.tasks.GenerateAddressTask
import com.simple.wallet.utils.exts.fromHex
import com.simple.wallet.utils.exts.isEvmAddress
import com.simple.wallet.utils.exts.isEvmPrivateKey
import com.simple.wallet.utils.exts.toHex
import wallet.core.jni.AnyAddress
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet
import wallet.core.jni.PrivateKey

class EvmGenerateAddressTask : GenerateAddressTask {

    override suspend fun executeTask(param: GenerateAddressTask.Param): Map<String, Chain.Type> {

        return if (param.walletType == Wallet.Type.SEED_PHASE) {

            mapOf(HDWallet(param.data, "").getAddressForCoin(CoinType.ETHEREUM).lowercase() to Chain.Type.EVM)
        } else if (param.walletType == Wallet.Type.PRIVATE && param.data.isEvmPrivateKey()) {

            mapOf(AnyAddress(PrivateKey(param.data.fromHex()).getPublicKeySecp256k1(false), CoinType.ETHEREUM).data().toHex().lowercase() to Chain.Type.EVM)
        } else if (param.walletType == Wallet.Type.ADDRESS && param.data.isEvmAddress()) {

            mapOf(param.data to Chain.Type.EVM)
        } else {

            emptyMap()
        }
    }
}