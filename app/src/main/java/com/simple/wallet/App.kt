package com.simple.wallet

import com.simple.coreapp.BaseApp

class App : BaseApp() {

    init {
        System.loadLibrary("TrustWalletCore")
    }

}