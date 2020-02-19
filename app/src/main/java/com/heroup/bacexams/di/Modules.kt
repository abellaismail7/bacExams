package com.heroup.bacexams.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.room.Room
import com.heroup.bacexams.local.AppDatabase
import com.heroup.bacexams.remote.WebApi
import com.heroup.bacexams.repository.Repository
import com.heroup.bacexams.ui.exam.MainViewModel
import com.heroup.bacexams.ui.intro.IntroViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val applicationModule = module(override = true) {

	single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidContext()) }

	factory { Repository( get(),get(),get(),get() ) }

	single {
		Room.databaseBuilder(androidContext(),
				AppDatabase::class.java, "app-database").build()
	}

	factory { androidContext().filesDir }

	factory { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

	factory { provideWebApi(get()) }

	single {
		Retrofit.Builder()
				.baseUrl("https://edu.clashbyte.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build()
	}

	viewModel { MainViewModel(get()) }
	viewModel { IntroViewModel(get()) }
}


fun provideWebApi(retrofit: Retrofit): WebApi = retrofit.create(WebApi::class.java)
