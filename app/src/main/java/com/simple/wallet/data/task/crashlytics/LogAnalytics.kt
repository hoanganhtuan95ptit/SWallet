package com.simple.wallet.data.task.crashlytics

import com.simple.analytics.Analytics

class LogAnalytics : Analytics {

    override suspend fun execute(vararg params: Pair<String, String>) {

//        Log.d("tuanha", "ANALYTICS: ${params.toMap().toJson()}")
    }
}