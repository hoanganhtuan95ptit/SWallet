package com.simple.wallet.data.repositories

import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.repositories.ChainRepository

class ChainRepositoryImpl :ChainRepository{

    override fun getListChain(values: Array<Chain.Type>): List<Chain>{

        return emptyList()
    }
}
