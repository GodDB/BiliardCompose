package com.example.biliardcompose

import android.app.Application
import com.example.biliardcompose.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class BiliardApplication : Application() {

    override fun onCreate() {
        super.onCreate()

     startKoin {
         androidContext(this@BiliardApplication)
         modules(listOf(viewModelModule))
     }
    }
}
