//package com.simple.wallet.presentation.transaction.send
//
//import android.util.Log
//import android.util.Range
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MediatorLiveData
//import com.simple.core.utils.extentions.validate
//import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
//import com.simple.coreapp.utils.extentions.combineSources
//import com.simple.coreapp.utils.extentions.get
//import com.simple.coreapp.utils.extentions.getOrEmpty
//import com.simple.coreapp.utils.extentions.postDifferentValue
//import com.simple.coreapp.utils.extentions.postValue
//import com.simple.state.ResultState
//import com.simple.state.doFailed
//import com.simple.state.doSuccess
//import com.simple.state.isStart
//import com.simple.state.toSuccess
//import com.simple.wallet.GAS_LIMIT_DEFAULT
//import com.simple.wallet.domain.entities.Chain
//import com.simple.wallet.domain.entities.Gas
//import com.simple.wallet.domain.entities.Token
//import com.simple.wallet.domain.entities.Transaction
//import com.simple.wallet.domain.entities.Wallet
//import com.simple.wallet.domain.usecases.transaction.GetGasAsyncUseCase
//import com.simple.wallet.domain.usecases.transaction.GetGasLimitAsyncUseCase
//import org.koin.core.component.inject
//import java.math.BigDecimal
//import java.math.BigInteger
//
//abstract class TransactionViewModel : BaseViewModel() {
//
//    private val getGasAsyncUseCase: GetGasAsyncUseCase by inject()
//
//    private val getGasLimitAsyncUseCase: GetGasLimitAsyncUseCase by inject()
//
//
//    open val gasDefaultId: String? = null
//
//
//    val nativeToken: LiveData<Token> = MediatorLiveData()
//
//    val currentChain: LiveData<Chain> = MediatorLiveData()
//
//    var currentWallet: LiveData<Wallet> = MediatorLiveData()
//
//
//    val customNonce: LiveData<BigInteger> = MediatorLiveData()
//
//
//    val gasList: LiveData<List<Gas>> = combineSources({
//
//        listOf(currentChain)
//    }) {
//
//        getGasAsyncUseCase.execute(GetGasAsyncUseCase.Param(currentChain.get().id)).collect { list ->
//
//            val gasList = if (gasDefaultId == null) list else list.validate { it.isDefault = it.id == gasDefaultId }
//
//            postDifferentValue(gasList)
//        }
//    }
//
//    val gas: LiveData<Gas> = combineSources({
//
//        listOf(gasList)
//    }) {
//
//        val gasList = gasList.get()
//
//        if (value == null) {
//
//            postValue(gasList.find { it.isDefault } ?: gasList.first())
//        } else if (value != null && !value!!.isCustom) {
//
//            postValue(gasList.find { it.id == value!!.id })
//        }
//    }
//
//
//    open val transaction: LiveData<Transaction> = MediatorLiveData()
//
//    val gasLimitState: LiveData<ResultState<Pair<BigInteger, Range<BigInteger>>>> = combineSources({
//
//        listOf(transaction)
//    }) {
//
//        val transaction = transaction.get()
//
//        GetGasLimitAsyncUseCase.Param(transaction = transaction).let {
//
//            getGasLimitAsyncUseCase.execute(it)
//        }.collect { state ->
//
//            state.doSuccess {
//
//                postValue(ResultState.Success(Pair(it.first, Range(it.second.first, it.second.second))))
//            }
//
//            state.doFailed { cause ->
//
//                postValue(ResultState.Failed(cause))
//            }
//        }
//    }.apply {
//
//        postValue(ResultState.Start)
//    }
//
//    val gasLimitDefault: LiveData<BigInteger> = combineSources({
//
//        listOf(gasLimitState)
//    }) {
//
//        val state = gasLimitState.get()
//
//        if (state.isStart()) return@combineSources
//
//        val gasLimit = state.toSuccess()?.data?.first ?: GAS_LIMIT_DEFAULT
//
//        postDifferentValue(gasLimit)
//    }
//
//    val gasLimit: LiveData<BigInteger> = combineSources({
//
//        listOf(gas, gasLimitDefault)
//    }) {
//
//        val gasLimitDefault = gasLimitDefault.get()
//
//        if (value == null || !gas.get().isCustom) {
//
//            postDifferentValue(gasLimitDefault)
//        }
//    }
//
//
//    open val bonusFeeState: LiveData<ResultState<BigDecimal>> = MediatorLiveData()
//
//    val bonusFee: LiveData<BigDecimal> = combineSources<BigDecimal>({
//
//        listOf(bonusFeeState)
//    }) {
//
//        val state = bonusFeeState.get()
//
//        state.doSuccess {
//
//            postDifferentValue(it)
//        }
//
//        state.doFailed {  _ ->
//
//            postDifferentValue(BigDecimal.ZERO)
//        }
//    }.apply {
//
//        postValue(BigDecimal.ZERO)
//    }
//
//    open fun clearSetting() {
//
//        Log.d("tuanha", "clearSetting: ")
//        gas.postValue(gasList.getOrEmpty().find { it.isDefault } ?: return)
//
//        customNonce.postValue(null)
//    }
//
//    open fun updateNativeToken(token: Token): Boolean {
//
//        if (this.nativeToken.value?.address.equals(token.address, true) && this.nativeToken.value?.symbol.equals(token.symbol, true) && this.nativeToken.value?.chainId == token.chainId) {
//            return false
//        }
//
//        return this.nativeToken.postDifferentValue(token) { old, new ->
//            old?.address.equals(new.address, true) && old?.symbol.equals(new.symbol, true) && old?.chainId == new.chainId
//        }
//    }
//
//    open fun updateCurrentChain(chain: Chain): Boolean {
//
//        if (this.currentChain.value?.id == chain.id) {
//            return false
//        }
//
//        clearSetting()
//
//        return this.currentChain.postDifferentValue(chain) { old, new ->
//            old?.id == new.id
//        }
//    }
//
//    open fun updateCurrentWallet(wallet: Wallet): Boolean {
//
//        if (this.currentWallet.value?.address.equals(wallet.address, true)) {
//            return false
//        }
//
//        clearSetting()
//
//        return this.currentWallet.postDifferentValue(wallet) { old, new ->
//            old?.address.equals(new.address, true)
//        }
//    }
//
//    open fun updateSettingInfo(gas: Gas?, gasLimitCustom: BigDecimal?, nonceCustom: BigInteger?) {
//
//        Log.d("tuanha", "updateSettingInfo: ")
//
//        if (gas == null) return
//
//        Log.d("tuanha", "updateSettingInfo: ${gas.id} gasLimitCustom:$gasLimitCustom customNonce:$nonceCustom")
//
//        this.gas.postValue(gas)
//
//        if (gasLimitCustom != null) {
//            this.gasLimit.postValue(gasLimitCustom.toBigInteger())
//        }
//
//        if (nonceCustom != null && nonceCustom > BigInteger.ZERO) {
//            this.customNonce.postDifferentValue(nonceCustom)
//        }
//    }
//}