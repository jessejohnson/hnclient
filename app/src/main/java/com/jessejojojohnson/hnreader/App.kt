package com.jessejojojohnson.hnreader

import android.app.Application
import com.jessejojojohnson.hnreader.data.HNDBService
import com.jessejojojohnson.hnreader.network.HNService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, persistenceModule)
        }
    }
}

val networkModule = module {
    single { HNService.get() }
}

val persistenceModule = module {
    single { HNDBService.get(androidContext()) }
}