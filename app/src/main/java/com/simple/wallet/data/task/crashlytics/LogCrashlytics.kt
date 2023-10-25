package com.simple.wallet.data.task.crashlytics

import android.util.Log
import com.simple.crashlytics.Crashlytics

class LogCrashlytics : Crashlytics {

    override suspend fun execute(throwable: Throwable) {

       Log.d("tuanha", "CRASHLYTICS", throwable)
    }
}