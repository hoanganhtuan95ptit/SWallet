package com.simple.wallet.data.task.token

import android.content.Context
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.core.utils.extentions.toListOrEmpty
import com.simple.wallet.R
import com.simple.wallet.data.cache.AppCache
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.utils.exts.isAddress
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class DefaultTokenSyncTask(
    private val context: Context,
    private val appCache: AppCache,
) : TokenSyncTask {

    private val version = 1L

    override suspend fun executeTask(param: Unit): List<Token> {

        val versionCache = appCache.getLong(DEFAULT_TOKEN_SYNC_TASK) ?: 0

        if (version <= versionCache) return emptyList()

        val tokenRepositoryList = readTextFile(context.resources.openRawResource(R.raw.token)).toListOrEmpty<TokenResponse>().apply {

            appCache.putLong(DEFAULT_TOKEN_SYNC_TASK, version)
        }


        return tokenRepositoryList.flatMap { tokenResponse ->

            tokenResponse.chainIdAndDecimalAndAddressMap.mapNotNull { item ->

                if (!item.address.isAddress(item.chainId)) {

                    return@mapNotNull null
                } else return@mapNotNull Token(

                    address = item.address,
                    symbol = tokenResponse.symbol,
                    name = tokenResponse.name,
                    decimals = item.decimal,
                    logo = tokenResponse.logo,
                    chainId = item.chainId,
                    tag = Token.Tag.UNKNOWN,
                    type = Token.Type.ERC_20,
                    geckoId = tokenResponse.gecko_id
                )
            }
        }
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
    private data class TokenResponse(

        var name: String = "",
        var symbol: String = "",

        var gecko_id: String = "",

        var logo: String = "",

        var rank: Int = 0,

        var chainIdAndDecimalAndAddressMap: List<ChainIdDecimalAddressResponse> = emptyList(),
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ChainIdDecimalAddressResponse(
        var chainId: Long = 0,
        var decimal: Int = 0,
        var address: String = "",
    )

    companion object {

        private const val DEFAULT_TOKEN_SYNC_TASK = "DEFAULT_TOKEN_SYNC_TASK"
    }
}