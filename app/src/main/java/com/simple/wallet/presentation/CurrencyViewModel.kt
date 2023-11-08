package com.simple.wallet.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.wallet.domain.entities.Currency

open class CurrencyViewModel : BaseViewModel() {

    val currency: LiveData<Currency> = MediatorLiveData<Currency>().apply {

        value = Currency.usd
    }
}