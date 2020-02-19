package com.heroup.bacexams.repository

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.heroup.bacexams.R
import com.heroup.bacexams.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class NetworkBoundResource<T>(uiScope: CoroutineScope) {
	private val result = MutableLiveData<Resource<T>>()
	init {
		uiScope.launch {

			setValue(Resource.Loading())

			withContext(Dispatchers.IO){

				val dbResult = loadFromDb()

				if (shouldFetch(dbResult)) fetchFromNetwork()

				else setValue(Resource.Success(dbResult))

			}

		}


	}

	private suspend fun fetchFromNetwork() {

		try {

			Log.d(NetworkBoundResource::class.java.name, "Fetch data from network")
			//setValue(Resource.Loading()) // Dispatch latest value quickly (UX purpose)
			val apiResponse = createCall()
			Log.e(NetworkBoundResource::class.java.name, "Data fetched from network")
			saveCallResult(apiResponse)
			setValue(Resource.Success(apiResponse))

		}catch (e:HttpException){
			setValue(Resource.Error(R.string.not_found, R.drawable.ic_404))
		} catch (e: Exception) {

			Log.e("NetworkBoundResource", "No_Internet")
			setValue(Resource.Error())

		}


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

	@WorkerThread
	protected abstract suspend fun saveCallResult(item: T)

	@MainThread
	protected abstract fun shouldFetch(data: T?): Boolean

	@MainThread
	protected abstract suspend fun loadFromDb(): T

	@MainThread
	protected abstract suspend fun createCall(): T
}