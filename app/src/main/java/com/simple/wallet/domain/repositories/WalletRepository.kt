package com.simple.wallet.domain.repositories

import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet

interface WalletRepository {

    fun generateMnemonic(): String


    fun importWallet(name: String, key: String, type: Wallet.Type, addressAndChainType: Map<String, Chain.Type>): Wallet


    fun getListWallet(isSupportAllChain: Boolean, walletTypeList: List<Wallet.Type>): List<Wallet>


    fun getWalletBy(walletAddress: String): Wallet

    fun getWalletSelected(isSupportAllChain: Boolean): Wallet


    suspend fun signMessage(request: Request): ResultState<String>
}