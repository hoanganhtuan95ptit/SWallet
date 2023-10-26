package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Chain

interface ChainRepository {

    fun getListChain(values: Array<Chain.Type>): List<Chain>
}
