package com.simple.wallet.domain.repositories

import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    fun generateMnemonic(): String


    fun importWallet(name: String, key: String, type: Wallet.Type, addressAndChainType: Map<String, Chain.Type>): Wallet


    fun getWalletList(isSupportAllWallet: Boolean, vararg walletTypeList: Wallet.Type = Wallet.Type.values()): List<Wallet>


    fun getWalletListAsync(isSupportAllWallet: Boolean, vararg walletTypeList: Wallet.Type = Wallet.Type.values()): Flow<List<Wallet>>


    fun getWalletBy(walletAddress: String): Wallet

    fun getWalletSelected(isSupportAllChain: Boolean): Wallet


    suspend fun signMessage(request: Request): ResultState<String>
}