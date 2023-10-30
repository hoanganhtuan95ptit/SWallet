package com.simple.wallet.presentation.wallet.add.import

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.state.ResultState
import com.simple.task.executeAsyncByFast
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.tasks.WalletTypeDetectTask
import org.jetbrains.annotations.VisibleForTesting

class ImportWalletViewModel(
    private val walletTypeDetectTasks: List<WalletTypeDetectTask>
) : BaseViewModel() {

    @VisibleForTesting
    val inputKey: LiveData<String> = MediatorLiveData()

    val walletType: LiveData<ResultState<Wallet.Type>> = combineSources(inputKey) {

        val inputKey = inputKey.get()

        if (inputKey.isBlank()) {

            postDifferentValue(ResultState.Failed(AppException(ImportWalletErrorCode.EMPTY)))
            return@combineSources
        }

        postDifferentValue(ResultState.Start)

        postDifferentValue(walletTypeDetectTasks.executeAsyncByFast(inputKey))
    }

    fun updateInputKey(value: String) {

        inputKey.postDifferentValue(value)
    }

    enum class ImportWalletErrorCode {

        EMPTY
    }
}