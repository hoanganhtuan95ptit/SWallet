package com.simple.wallet

import android.content.Context
import androidx.startup.Initializer
import com.simple.wallet.di.apiModule
import com.simple.wallet.di.daoModule
import com.simple.wallet.di.navigationModule
import com.simple.wallet.di.repositoryModule
import com.simple.wallet.di.taskModule
import com.simple.wallet.di.useCaseModule
import com.simple.wallet.di.viewModelModule
import com.simpleø.wallet.di.cacheModule
import org.koin.core.context.loadKoinModules

class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {

        loadKoinModules(
            listOf(
                apiModule,
                daoModule,
                taskModule,
                cacheModule,
                useCaseModule,
                viewModelModule,
                repositoryModule,
                navigationModule,
            )
        )

        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}