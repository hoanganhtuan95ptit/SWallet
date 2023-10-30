package com.simple.wallet.data.repositories

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.simple.state.ResultState
import com.simple.task.executeAsyncByPriority
import com.simple.wallet.data.cache.AppCache
import com.simple.wallet.data.dao.wallet.WalletDao
import com.simple.wallet.data.task.sign.SignParam
import com.simple.wallet.data.task.sign.SignTask
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.WalletRepository
import com.simple.wallet.utils.exts.fromHex
import com.simple.wallet.utils.exts.shortenValue
import com.simple.wallet.utils.exts.takeIfNotBlank
import com.simple.wallet.utils.exts.toCoinType
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet
import wallet.core.jni.PrivateKey
import wallet.core.jni.StoredKey
import java.io.File
import java.security.SecureRandom
import java.util.UUID

class WalletRepositoryImpl(
    private val context: Context,

    private val appCache: AppCache,
    private val walletDao: WalletDao,

    private val signTaskList: List<SignTask>
) : WalletRepository {

    private val PREF_FILE_NAME = "wallet_pref"
    private val TINK_KEYSET_NAME = "wallet_keyset"
    private val MASTER_KEY_URI = "android-keystore://wallet_master_key"

    private val keysetHandle by lazy {

        AndroidKeysetManager.Builder()
            .withSharedPref(context, TINK_KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
    }

    private val aead by lazy {

        AeadConfig.register()

        keysetHandle.getPrimitive(Aead::class.java)
    }


    override fun generateMnemonic(): String {

        return HDWallet(128, "").mnemonic()
    }


    override fun importWallet(name: String, key: String, type: Wallet.Type, addressAndChainType: Map<String, Chain.Type>): Wallet {

        val walletList = walletDao.findWalletListBy(addressAndChainType)

        val wallet = if (walletList.size == 1) {
            walletList.first()
        } else {
            Wallet()
        }

        if (type == Wallet.Type.SEED_PHASE && type != wallet.type) {

            val coinType = CoinType.ETHEREUM

            val generatedPassword = generatePassword()

            wallet.id = StoredKey.importHDWallet(key, name, generatedPassword, coinType).insertStoredKey()
            wallet.cipher = cipher(generatedPassword)
        } else if (type == Wallet.Type.PRIVATE && type != wallet.type) {

            val coinType = addressAndChainType.toList().first().second.toCoinType()

            val generatedPassword = generatePassword()

            wallet.id = StoredKey.importPrivateKey(key.fromHex(), name, generatedPassword, coinType).identifier()
            wallet.cipher = cipher(generatedPassword)
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

    override fun getListWallet(isSupportAllChain: Boolean, walletTypeList: List<Wallet.Type>): List<Wallet> {

        val list = arrayListOf<Wallet>()

        if (isSupportAllChain) {

            list.add(Wallet.ALL)
        }

        list.addAll(walletDao.findWalletListBy(*walletTypeList.toTypedArray()))

        return list
    }


    override fun getWalletBy(walletAddress: String): Wallet {

        return walletDao.findWalletListByAddress(walletAddress).first()
    }

    override fun getWalletSelected(isSupportAllChain: Boolean): Wallet {

        val walletId = (if (isSupportAllChain) {
            appCache.getString(ALL_WALLET_SELECTED) ?: Wallet.ID_ALL
        } else {
            appCache.getString(WALLET_SELECTED) ?: ""
        })

        return if (walletId == Wallet.ID_ALL) {

            Wallet.ALL
        } else {

            walletDao.findWalletListById(walletId).firstOrNull() ?: walletDao.findWalletListBy(*Wallet.Type.values()).first()
        }
    }


    override suspend fun signMessage(request: Request): ResultState<String> {

        val walletAddress = request.walletAddress ?: error("")

        val wallet = walletDao.findWalletListByAddress(walletAddress).first()

        val privateKey = getPrivateKey(wallet)

        return signTaskList.executeAsyncByPriority(SignParam(privateKey, request))
    }


    private fun generatePassword(): ByteArray {

        val bytes = ByteArray(16)
        val random = SecureRandom()

        random.nextBytes(bytes)

        Base64.encodeToString(bytes, Base64.DEFAULT)

        return Base64.encodeToString(bytes, Base64.DEFAULT).toByteArray()
    }


    private fun getPrivateKey(wallet: Wallet): PrivateKey {

        return wallet.getStoredKey().privateKey(wallet.chainType.first().toCoinType(), decryptPassword(wallet.cipher).toByteArray())
    }

    private fun decryptPassword(walletCipher: String): String {

        return String(aead.decrypt(Base64.decode(walletCipher, Base64.DEFAULT), ByteArray(0)), Charsets.UTF_8)
    }

    private fun cipher(password: ByteArray): String {

        return Base64.encodeToString(aead.encrypt(password, ByteArray(0)), Base64.DEFAULT)
    }

    private fun Wallet.getStoredKey(): StoredKey {

        val walletId = id ?: error("")

        return StoredKey.load(getStoredKeyFile(walletId).absolutePath) ?: throw RuntimeException("Can't read the wallet file")
    }

    private fun StoredKey.insertStoredKey(): String {

        val walletId = identifier()

        store(getStoredKeyFile(walletId).absolutePath)

        return walletId
    }

    private fun getStoredKeyFile(walletId: String): File {

        val file = File(getDirectoryPath() + "/" + walletId + ".json")

        if (!file.exists()) {

            file.createNewFile()
        }

        return file
    }

    private fun getDirectoryPath(): String {

        val fileDirectory = File(context.filesDir, "wallets")

        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }

        return fileDirectory.absolutePath
    }
}

private val ALL_WALLET_SELECTED by lazy {
    "ALL_WALLET_SELECTED"
}

private val WALLET_SELECTED by lazy {
    "ALL_WALLET_SELECTED"
}