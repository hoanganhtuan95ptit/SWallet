package com.simple.wallet.data.task.crashlytics

import android.util.Log
import com.simple.analytics.Analytics
import com.simple.core.utils.extentions.toJson

class LogAnalytics : Analytics {

    override suspend fun execute(vararg params: Pair<String, String>) {

        Log.d("tuanha", "ANALYTICS: ${params.toMap().toJson()}")
    }
}