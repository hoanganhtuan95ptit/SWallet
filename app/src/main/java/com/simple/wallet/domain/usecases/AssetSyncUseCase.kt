package com.simple.wallet.domain.usecases

import com.simple.coreapp.data.usecase.BaseUseCase
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.coreapp.utils.extentions.offerActiveAwait
import com.simple.state.ResultState
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.BalanceRepository
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.TokenRepository
import com.simple.wallet.domain.repositories.WalletRepository
import com.simple.wallet.utils.exts.launchCollect
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class AssetSyncUseCase(
    private val tokenRepository: TokenRepository,
    private val chainRepository: ChainRepository,
    private val walletRepository: WalletRepository,
    private val balanceRepository: BalanceRepository
) : BaseUseCase<AssetSyncUseCase.Param, Flow<ResultState<Triple<Float, Chain, List<Token>>>>> {


    override suspend fun execute(param: Param?): Flow<ResultState<Triple<Float, Chain, List<Token>>>> = channelFlow {
        checkNotNull(param)


        offerActive(ResultState.Start)


        channelFlow {

            offerActiveAwait("")
        }.flatMapLatest {

            chainRepository.getChainListAsync(isSupportAllChain = false)
        }.flatMapLatest { chainList ->

            tokenRepository.getTokenListAsync().map { tokenList -> Pair(chainList, tokenList) }
        }.flatMapLatest { pair ->

            walletRepository.getWalletListAsync(isSupportAllWallet = false).map { walletList -> Triple(pair.first, pair.second, walletList) }
        }.debounce(1000).flatMapLatest {

            getBalanceAsync(param, it.first, it.third, it.second)
        }.debounce(350).launchCollect(this) {

            offerActive(ResultState.Success(it))
        }

        awaitClose {
        }
    }

    private fun getBalanceAsync(param: Param, chainList: List<Chain>, walletList: List<Wallet>, tokenList: List<Token>): Flow<Triple<Float, Chain, List<Token>>> {

        return if (walletList.isEmpty()) channelFlow {

            offerActiveAwait(Triple(0.1f, Chain.ETHEREUM, tokenList))
        } else combine(
            getBalanceAllAsync(chainList, walletList, tokenList),
            getBalanceByAsync(param, chainList, walletList, tokenList)
        ) { chainSyncBalanceCompleted, balanceList ->

            Triple(chainSyncBalanceCompleted.first, chainSyncBalanceCompleted.second, balanceList)
        }
    }

    private fun getBalanceByAsync(param: Param, chainList: List<Chain>, walletAll: List<Wallet>, tokenAll: List<Token>): Flow<List<Token>> {

        val walletList = if (param.wallet.isAllWallet) {

            walletAll
        } else {

            listOf(param.wallet)
        }

        val idAndToken = tokenAll.associateBy {

            Pair(it.chainId, it.address)
        }

        val idAndChain = chainList.associateBy {

            it.id
        }


        return (if (param.chain.isAllNetwork) {

            balanceRepository.getBalanceListAsync(walletAddressList = walletList.map { it.address })
        } else {

            balanceRepository.getBalanceListAsync(chainIdList = listOf(param.chain.id), walletAddressList = walletList.map { it.address })
        }).distinctUntilChanged().map { list ->

            list.groupBy {

                Pair(it.chainId, it.tokenAddress)
            }.toList().mapNotNull {

                val token = idAndToken[it.first] ?: return@mapNotNull null

                token.walletAddressAndBalance = it.second.associateBy({ it.walletAddress }, { it.balance })

                token
            }
        }.flatMapLatest { tokenList ->

            val keyAndTokenMap = tokenList.associateBy { Pair(it.chainId, it.address) }

            tokenRepository.getTokenPriceAsync(tokenList).debounce(1000).map { tokenPriceList ->

                val keyAndPrice = tokenPriceList.associateBy { Pair(it.chainId, it.address) }

                keyAndTokenMap.map {

                    val token = it.value

                    token.chain = idAndChain[token.chainId]
                    token.price = keyAndPrice[it.key]

                    token
                }

                tokenList
            }
        }
    }

    private fun getBalanceAllAsync(chainAll: List<Chain>, walletAll: List<Wallet>, tokenAll: List<Token>) = chainRepository.getSmartContractListAsync(Chain.SmartContract.Type.MULTI_CALL_V3).map { smartContractAll ->


        val chainIdAndTokenList = tokenAll.filter {

            it.address.isNotBlank()
        }.groupBy {

            it.chainId
        }

        val chainTypeAndWalletList = walletAll.groupBy {

            it.chainType
        }

        val chainIdAndSmartContractList = smartContractAll.groupBy {

            it.chainId
        }


        chainAll.map {

            ChainMetaData(it, chainRepository.getRpcList(it.id), chainIdAndTokenList[it.id] ?: emptyList(), chainTypeAndWalletList[it.type] ?: emptyList(), chainIdAndSmartContractList[it.id] ?: emptyList())
        }
    }.flatMapLatest { chainIdAndChainMetadata ->

        getBalanceAllAsync(chainIdAndChainMetadata)
    }

    private fun getBalanceAllAsync(chainIdAndChainMetadata: List<ChainMetaData>) = channelFlow {

        offerActive(Pair(0f, Chain.ETHEREUM))

//        launch {
//
//            chainIdAndChainMetadata.flatMapIndexed { index, chainMetaData ->
//
//
//                val chain = chainMetaData.chain
//
//
//                val tokenList = chainMetaData.tokenList
//
//                Log.d("tuanha", "getBalanceAllAsync: ${chain.id}")
//
//                val listBalance = if (chainMetaData.smartContractList.isEmpty()) {
//
//                    balanceRepository.getBalanceList(chain, chainMetaData.rpcList, chainMetaData.walletList, tokenList)
//                } else {
//
//                    balanceRepository.getBalanceList(chain, chainMetaData.rpcList, chainMetaData.smartContractList, chainMetaData.walletList, tokenList)
//                }
//
//
//                val listBalanceCorrect = listBalance.filter { it.balance <= UNLIMITED }
//
//                val listBalanceNeedValidate = listBalance.toMutableList().apply {
//
//                    removeAll(listBalanceCorrect)
//                }.mapNotNull { balance ->
//
//                    tokenList.find { it.address == balance.tokenAddress }
//                }.let { list ->
//
//                    balanceRepository.getBalanceList(chain, chainMetaData.rpcList, chainMetaData.walletList, list)
//                }
//
//
//                balanceRepository.insert(*listBalanceCorrect.toTypedArray(), *listBalanceNeedValidate.toTypedArray())
//
//
//                Log.d("tuanha", "getBalanceAllAsync2: chainId:${chain.id}--index:$index--percent:${index * 1f / chainIdAndChainMetadata.size}--size:${listBalance.size}")
//
//
//                offerActive(Pair(index * 1f / chainIdAndChainMetadata.size, chain))
//
//
//                emptyList<Balance>()
//            }
//
//            24 * 60 * 60 * 1000L
//        }

        awaitClose {
        }
    }

    private fun <T> Flow<T>.debounce(time: Long) = channelFlow {

        var timePost = 0L

        this@debounce.launchCollect(this) {

            kotlinx.coroutines.delay(time - (System.currentTimeMillis() - timePost))

            offerActive(it)

            timePost = System.currentTimeMillis()
        }

        awaitClose { }
    }

    private data class ChainMetaData(
        val chain: Chain,

        val rpcList: List<String>,

        val tokenList: List<Token>,
        val walletList: List<Wallet>,
        val smartContractList: List<Chain.SmartContract>,
    )

    data class Param(val chain: Chain, val wallet: Wallet)
}