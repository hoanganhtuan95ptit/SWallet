package com.simple.wallet.data.task.token

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.wallet.domain.entities.Token
import retrofit2.Retrofit
import retrofit2.http.GET
import java.math.BigDecimal

class DefaultTokenPriceSyncTask(
    private val retrofit: Retrofit,
) : TokenPriceSyncTask {

    override suspend fun executeTask(param: List<Token>): List<Token.Price> {

        val tokenIdAndPrice = retrofit.create(TokenService::class.java).fetchPriceAwait().associateBy {
            it.id
        }

        return param.groupBy { it.geckoId }.flatMap { entry ->

            val priceResponse = tokenIdAndPrice[entry.key]

            val price = priceResponse?.current_price?.toBigDecimal() ?: BigDecimal.ZERO

            val priceChange = hashMapOf(
                Token.Price.Change.CHANGE_1H to (priceResponse?.price_change_percentage_1h_in_currency?.toBigDecimal() ?: BigDecimal.ZERO),
                Token.Price.Change.CHANGE_24H to (priceResponse?.price_change_percentage_24h_in_currency?.toBigDecimal() ?: BigDecimal.ZERO),
                Token.Price.Change.CHANGE_7D to (priceResponse?.price_change_percentage_7d_in_currency?.toBigDecimal() ?: BigDecimal.ZERO),
                Token.Price.Change.CHANGE_14D to (priceResponse?.price_change_percentage_14d_in_currency?.toBigDecimal() ?: BigDecimal.ZERO),
                Token.Price.Change.CHANGE_30D to (priceResponse?.price_change_percentage_30d_in_currency?.toBigDecimal() ?: BigDecimal.ZERO),
                Token.Price.Change.CHANGE_200D to (priceResponse?.price_change_percentage_200d_in_currency?.toBigDecimal() ?: BigDecimal.ZERO),
                Token.Price.Change.CHANGE_1Y to (priceResponse?.price_change_percentage_1y_in_currency?.toBigDecimal() ?: BigDecimal.ZERO)
            )

            entry.value.map {

                Token.Price(
                    chainId = it.chainId,
                    address = it.address,

                    price = price,
                    priceChange = priceChange
                )
            }
        }
    }

    private interface TokenService {

        @GET("https://raw.githubusercontent.com/hoanganhtuan95ptit/cryptodata/main/token/price.json")
        suspend fun fetchPriceAwait(): List<PriceResponse>
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class PriceResponse(
        val id: String = "",
        val current_price: Double = 0.0,
        val price_change_percentage_1h_in_currency: Double = 0.0,
        val price_change_percentage_24h_in_currency: Double = 0.0,
        val price_change_percentage_7d_in_currency: Double = 0.0,
        val price_change_percentage_14d_in_currency: Double = 0.0,
        val price_change_percentage_30d_in_currency: Double = 0.0,
        val price_change_percentage_200d_in_currency: Double = 0.0,
        val price_change_percentage_1y_in_currency: Double = 0.0
    )
}