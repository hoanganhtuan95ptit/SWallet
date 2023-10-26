package com.simple.wallet.domain.repositories

import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet

interface WalletRepository {

    fun generateMnemonic(): String

    fun getPrivateKey(walletAddress: String): String


    fun importWallet(name: String, key: String, type: Wallet.Type, addressAndChainType: Map<String, Chain.Type>): Wallet

    fun getListWallet(walletTypeList: List<Wallet.Type>): List<Wallet>
}