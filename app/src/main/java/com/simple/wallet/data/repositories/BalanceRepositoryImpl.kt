package com.simple.wallet.data.repositories

import com.one.web3.task.balance.BalanceTask
import com.one.web3.task.balancemulti.BalanceMultiTask
import com.one.web3.task.balancenative.BalanceNativeTask
import com.simple.state.toSuccess
import com.simple.task.executeSyncByPriority
import com.simple.wallet.data.dao.BalanceDao
import com.simple.wallet.domain.entities.Balance
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.repositories.BalanceRepository
import com.simple.wallet.utils.exts.takeIfNotEmpty
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

class BalanceRepositoryImpl(
    private val balanceDao: BalanceDao,

    private val balanceTask: List<BalanceTask>,
    private val balanceNativeTask: List<BalanceNativeTask>,
    private val balanceMultiEvmCallTask: List<BalanceMultiTask>
) : BalanceRepository {


    override fun getBalanceListAsync(walletAddressList: List<String>): Flow<List<Balance>> {

        return balanceDao.getListByAsync(walletAddressList)
    }


    override fun getBalanceListAsync(chainIdList: List<Long>, walletAddressList: List<String>): Flow<List<Balance>> {

        return balanceDao.getListByAsync(chainIdList, walletAddressList)
    }


    override suspend fun getBalanceList(chain: Chain, rpcList: List<String>, walletList: List<Wallet>, tokenList: List<Token>): List<Balance> {

        val list = arrayListOf<Balance>()

        tokenList.filter {

            it.type == Token.Type.NATIVE
        }.takeIfNotEmpty()?.flatMap { token ->

            walletList.map { wallet ->

                val balanceState = balanceNativeTask.executeSyncByPriority(BalanceNativeTask.Param(walletAddress = wallet.address, chainId = chain.id, rpcUrls = rpcList, true))

                val balance = balanceState.toSuccess()?.data ?: BigInteger.ZERO

                Balance(
                    chainId = chain.id,
                    tokenAddress = token.address,
                    walletAddress = wallet.address,

                    balance = balance,
                )
            }
        }?.let {

            list.addAll(it)
        }


        tokenList.filter {

            it.type == Token.Type.ERC_20
        }.takeIfNotEmpty()?.flatMap { token ->

            walletList.map { wallet ->

                val balanceState = balanceTask.executeSyncByPriority(BalanceTask.Param(tokenAddress = token.address, walletAddress = wallet.address, chainId = chain.id, rpcUrls = rpcList, true))

                val balance = balanceState.toSuccess()?.data ?: BigInteger.ZERO

                Balance(
                    chainId = chain.id,
                    tokenAddress = token.address,
                    walletAddress = wallet.address,

                    balance = balance,
                )
            }
        }?.let {

            list.addAll(it)
        }

        return list
    }

    override suspend fun getBalanceList(chain: Chain, rpcList: List<String>, smartContractList: List<Chain.SmartContract>, walletList: List<Wallet>, tokenList: List<Token>): List<Balance> {


        val list = arrayListOf<Balance>()

        tokenList.filter {

            it.type == Token.Type.NATIVE
        }.takeIfNotEmpty()?.flatMap { token ->

            walletList.map { wallet ->

                val balanceState = balanceNativeTask.executeSyncByPriority(BalanceNativeTask.Param(walletAddress = wallet.address, chainId = chain.id, rpcUrls = rpcList, true))

                val balance = balanceState.toSuccess()?.data ?: BigInteger.ZERO

                Balance(
                    chainId = chain.id,
                    tokenAddress = token.address,
                    walletAddress = wallet.address,

                    balance = balance,
                )
            }
        }?.let {

            list.addAll(it)
        }


        tokenList.filter {

            it.type == Token.Type.ERC_20
        }.takeIfNotEmpty()?.chunked(2000)?.flatMap { tokens ->

            val param = BalanceMultiTask.Param(

                tokenAddressList = tokens.map { it.address },
                walletAddressList = walletList.map { it.address },

                multiCallAddress = smartContractList.first().address,

                chainId = chain.id,
                rpcUrls = rpcList,
                sync = true
            )

            val balanceList = balanceMultiEvmCallTask.executeSyncByPriority(param).toSuccess()?.data?.map {

                Balance(
                    chainId = chain.id,
                    tokenAddress = it.key.second,
                    walletAddress = it.key.first,

                    balance = it.value,
                )
            }

            balanceList ?: emptyList()
        }?.let {

            list.addAll(it)
        }

        return list
    }


    override fun insert(vararg balance: Balance) {

        balanceDao.insert(*balance)
    }
}