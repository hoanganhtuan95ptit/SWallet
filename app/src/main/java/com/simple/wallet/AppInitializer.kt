package com.simple.wallet

import android.content.Context
import androidx.startup.Initializer
import com.simple.wallet.di.apiModule
import com.simple.wallet.di.cacheModule
import com.simple.wallet.di.daoModule
import com.simple.wallet.di.navigationModule
import com.simple.wallet.di.repositoryModule
import com.simple.wallet.di.socketModule
import com.simple.wallet.di.taskModule
import com.simple.wallet.di.useCaseModule
import com.simple.wallet.di.viewModelModule
import com.simple.wallet.di.viewObserveModule
import org.koin.core.context.loadKoinModules

class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {

        loadKoinModules(
            listOf(
                apiModule,
                daoModule,
                taskModule,
                cacheModule,
                socketModule,
                useCaseModule,
                viewModelModule,
                repositoryModule,
                navigationModule,
                viewObserveModule,
            )
        )

        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}