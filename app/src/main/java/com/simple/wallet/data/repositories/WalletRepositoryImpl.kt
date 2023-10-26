package com.simple.wallet.data.repositories

import android.util.Base64
import com.simple.wallet.data.dao.WalletDao
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository
import com.simple.wallet.utils.exts.fromHex
import com.simple.wallet.utils.exts.shortenValue
import com.simple.wallet.utils.exts.takeIfNotBlank
import com.simple.wallet.utils.exts.toCoinType
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet
import wallet.core.jni.StoredKey
import java.security.SecureRandom
import java.util.UUID

class WalletRepositoryImpl(
    private val walletDao: WalletDao
) : WalletRepository {

    override fun generateMnemonic(): String {

        return HDWallet(128, "").mnemonic()
    }

    override fun getPrivateKey(walletAddress: String): String {

        return ""
    }

    override fun importWallet(name: String, key: String, type: Wallet.Type, addressAndChainType: Map<String, Chain.Type>): Wallet {

        val walletList = walletDao.findWalletListBy(addressAndChainType)

        val wallet = if (walletList.size == 1) {
            walletList.first()
        } else {
            Wallet()
        }

        if (type == Wallet.Type.SEED_PHASE && type != wallet.type) {

            val generatedPassword = generatePassword()

            val storedKey = StoredKey.importHDWallet(key, name, generatedPassword, CoinType.ETHEREUM)

            wallet.id = storedKey.identifier()
        } else if (type == Wallet.Type.PRIVATE && type != wallet.type) {

            val chainType = addressAndChainType.toList().first().second

            val generatedPassword = generatePassword()

            val storedKey = StoredKey.importPrivateKey(key.fromHex(), name, generatedPassword, chainType.toCoinType())

            wallet.id = storedKey.identifier()
        } else {

            wallet.id = wallet.id ?: UUID.randomUUID().toString()
        }


        val nameWrap = name.takeIfNotBlank() ?: wallet.name.takeIfNotBlank() ?: addressAndChainType.toList().first().first.shortenValue()


        wallet.name = nameWrap
        wallet.type = type
        wallet.addressMap = addressAndChainType


        walletDao.deleteBy(walletList.mapNotNull { it.id })

        walletDao.insert(wallet)

        return wallet
    }

    override fun getListWallet(walletTypeList: List<Wallet.Type>): List<Wallet> {

        return walletDao.findWalletListBy(*walletTypeList.toTypedArray())
    }

    private fun generatePassword(): ByteArray {

        val bytes = ByteArray(16)
        val random = SecureRandom()

        random.nextBytes(bytes)

        Base64.encodeToString(bytes, Base64.DEFAULT)

        return Base64.encodeToString(bytes, Base64.DEFAULT).toByteArray()
    }
}