package com.simple.wallet.domain.entities

import java.math.BigDecimal

data class Currency(
    val logo: String = "",
    val name: String = "",
    val price: BigDecimal = BigDecimal.ZERO,
    val format: String = "",
) {

    companion object {

        val usd by lazy {

            Currency(
                logo = "https://iconape.com/wp-content/png_logo_vector/logo-usd.png",
                name = "USD",
                price = BigDecimal.ONE,
                format = "$%s"
            )
        }
    }
}