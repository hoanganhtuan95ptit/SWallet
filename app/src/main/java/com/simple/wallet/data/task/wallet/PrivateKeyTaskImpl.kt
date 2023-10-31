package com.simple.wallet.data.task.wallet

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.one.web3.task.privatekey.PrivateKeyTask
import com.simple.wallet.data.dao.wallet.WalletDao
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.utils.exts.toCoinType
import com.simple.wallet.utils.exts.toHex
import wallet.core.jni.StoredKey
import java.io.File

class PrivateKeyTaskImpl(
    private val context: Context,
    private val walletDao: WalletDao
): PrivateKeyTask {


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


    override suspend fun executeTask(param: PrivateKeyTask.Param): String {

        val wallet = walletDao.findWalletListByAddress(param.walletAddress).first()

        return wallet.getStoredKey().privateKey(wallet.chainType.first().toCoinType(), decryptPassword(wallet.cipher).toByteArray()).data().toHex()
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

        Log.d("tuanha", "getStoredKeyFile: ${file.absolutePath}")

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