package com.simple.wallet

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.wallet.domain.usecases.SyncUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val syncUseCase: SyncUseCase
) : BaseViewModel() {

    init {

        viewModelScope.launch(handler + Dispatchers.IO) {

            syncUseCase.execute().collect{

            }
        }
    }
}