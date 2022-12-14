package com.jessejojojohnson.hnreader

import android.app.Application
import com.jessejojojohnson.hnreader.network.HNService
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(networkModule)
        }
    }
}

val networkModule = module {
    single { HNService.get() }
}