package com.simple.wallet.data.task.chain

import android.content.Context
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.core.utils.extentions.toListOrEmpty
import com.simple.wallet.R
import com.simple.wallet.data.cache.AppCache
import com.simple.wallet.data.dao.chain.ChainDao
import com.simple.wallet.data.dao.chain.RpcChainDao
import com.simple.wallet.data.dao.chain.SmartContractDao
import com.simple.wallet.data.dao.token.TokenDao
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Chain.Companion.toChainType
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Token.Companion.TOKEN_NATIVE_ADDRESS_DEFAULT
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class DefaultChainSyncTask(
    private val context: Context,
    private val appCache: AppCache,

    private val chainDao: ChainDao,
    private val tokenDao: TokenDao,
    private val rpcChainDao: RpcChainDao,
    private val smartContractDao: SmartContractDao
) : ChainSyncTask {

    private val version = 1L

    override suspend fun executeTask(param: Unit) {

        val versionCachePhonetics = appCache.getVersionCachePhonetics()

//        if (version <= versionCachePhonetics) return

        var chainRepositoryList = readTextFile(context.resources.openRawResource(R.raw.chain)).toListOrEmpty<ChainResponse>().apply {

            appCache.saveVersionCachePhonetics(version)
        }


        chainRepositoryList = chainRepositoryList.filter { chain ->

            if (chain.type !in Chain.Type.values().map { it.value }) return@filter false

            if (chain.urls.find { it.type == RPC } == null) return@filter false

            if (chain.urls.find { it.type == BLOCK_EXPLORER } == null) return@filter false

            return@filter true
        }


        val tokenList = arrayListOf<Token>()


        val chainList = arrayListOf<Chain>()

        val chainRpcList = arrayListOf<Chain.Rpc>()

        val smartContractChainList = arrayListOf<Chain.SmartContract>()


        chainRepositoryList.forEachIndexed { _, chain ->

            val explorer = chain.urls.first {

                it.type == BLOCK_EXPLORER
            }.let {

                Chain.Explorer(
                    url = it.url,
                    name = it.name
                )
            }


            Chain(
                id = chain.id,
                name = chain.name,
                image = chain.logo,

                type = chain.type.toChainType(),

                explorer = explorer,
            ).let {

                chainList.add(it)
            }


            chain.urls.filter {

                it.type == RPC
            }.map {

                Chain.Rpc(
                    chainId = chain.id,
                    priority = it.priority?.toIntOrNull() ?: 0,
                    url = it.url,
                    name = it.name
                )
            }.let {

                chainRpcList.addAll(it)
            }


            chain.smartContracts.map {

                Chain.SmartContract(
                    chainId = chain.id,
                    address = it.address,
                    type = it.type
                )
            }.let {

                smartContractChainList.addAll(it)
            }

            chain.nativeToken?.let {

                Token(
                    address = TOKEN_NATIVE_ADDRESS_DEFAULT,

                    symbol = it.symbol,

                    name = it.name,

                    decimals = it.decimals.toInt(),

                    logo = "",

                    chainId = chain.id,

                    type = Token.Type.NATIVE,

                    tag = Token.Tag.VERIFIED,

                    geckoId = it.geckoId
                )
            }?.let {

                tokenList.add(it)
            }
        }

        tokenDao.insert(tokenList)

        chainDao.insert(chainList)

        rpcChainDao.insert(chainRpcList)

        smartContractDao.insert(smartContractChainList)
    }

    private fun readTextFile(inputStream: InputStream): String {

        val outputStream = ByteArrayOutputStream()

        val buf = ByteArray(1024)

        var len: Int

        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (_: IOException) {
        }

        return outputStream.toString()
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ChainResponse(

        var id: Long = 0,
        var type: String = "",
        var name: String = "",

        var logo: String = "",

        var nativeToken: TokenResponse? = null,

        var urls: List<UrlResponse> = emptyList(),
        var configs: List<ConfigResponse> = emptyList(),
        var smartContracts: List<SmartContractResponse> = emptyList(),
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class TokenResponse(
        var name: String = "",
        var symbol: String = "",
        var geckoId: String = "",
        var decimals: Long = 0
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class UrlResponse(
        var url: String = "",
        var type: String = "",
        var name: String = "",
        var priority: String? = null
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ConfigResponse(
        var name: String = "",
        var value: String = ""
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class SmartContractResponse(
        var address: String = "",
        var type: String = ""
    )
}

private val RPC by lazy {
    "RPC"
}

private val BLOCK_EXPLORER by lazy {
    "BLOCK_EXPLORER_URL"
}