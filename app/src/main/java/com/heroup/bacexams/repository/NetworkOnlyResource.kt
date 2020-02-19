package com.heroup.bacexams.repository

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.heroup.bacexams.R
import com.heroup.bacexams.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

abstract class NetworkOnlyResource<T>(uiScope: CoroutineScope) {
	private val result = MutableLiveData<Resource<T>>()
	init {
		uiScope.launch {

			setValue(Resource.Loading())

			withContext(Dispatchers.IO){
				try {
					fetchFromNetwork()
				} catch (e: HttpException) {
					Log.e("NetworkOnlyResource", "${e.stackTrace}")
					setValue(Resource.Error(R.string.not_found,R.drawable.ic_404))

				}catch (e:Exception){
					Log.e("NetworkOnlyResource", "NO INTERNET")
					setValue(Resource.Error())
				}
			}

		}


	}

	private suspend fun fetchFromNetwork() {
			val apiResponse = createCall()
			setValue(Resource.Success(apiResponse))
	}


	@MainThread
	suspend fun setValue(newValue: Resource<T>) {
		withContext(Dispatchers.Main){
			if (result.value != newValue) {
				result.value = newValue
			}
		}
	}

	fun asLiveData() = result as LiveData<Resource<T>>




	@MainThread
	protected abstract suspend fun createCall(): T
}