package com.jessejojojohnson.hnreader

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkManager
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
            modules(networkModule, persistenceModule, workManagerModule)
        }
    }
}

val networkModule = module {
    single { HNService.get() }
}

val persistenceModule = module {
    single { HNDBService.get(androidContext()) }
    single { HNDBService.getDataStore(androidContext()) }
}

val workManagerModule = module {
    single { WorkManager.getInstance(androidContext()) }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("article_cache")