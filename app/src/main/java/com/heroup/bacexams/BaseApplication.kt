package com.heroup.bacexams

import android.app.Application
import com.heroup.bacexams.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@Suppress("unused")
class BaseApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		startKoin{
			androidLogger()
			androidContext(this@BaseApplication)
			// modules
			modules(listOf(applicationModule))}
	}
}