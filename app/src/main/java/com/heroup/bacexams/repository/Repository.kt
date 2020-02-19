package com.heroup.bacexams.repository

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.heroup.bacexams.R
import com.heroup.bacexams.local.AppDatabase
import com.heroup.bacexams.models.Material
import com.heroup.bacexams.models.Orient
import com.heroup.bacexams.remote.WebApi
import com.heroup.bacexams.util.FileResource
import com.heroup.bacexams.util.Resource
import kotlinx.coroutines.*
import java.io.File

class Repository(
		private val webApi: WebApi,
		db : AppDatabase,
		private val fileDir: File,
		private val connectivityManager: ConnectivityManager) {


	private var viewModelJob = Job()
	private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
	private val storage = FirebaseStorage.getInstance()
	private val byteArray = MutableLiveData<FileResource<ByteArray>>()
	private val materialDoa = db.materialDoa()
	private val orientDao = db.orientDao()

	fun loadMaterials(orient:String) : LiveData<Resource<List<Material>>> {

		return object :NetworkBoundResource<List<Material>>(uiScope){

			override fun shouldFetch(data: List<Material>?): Boolean = data.isNullOrEmpty()

			override suspend fun loadFromDb(): List<Material> = materialDoa.getMaterials(orient)

			override suspend fun createCall(): List<Material> = webApi.getMaterials(orient)

			override suspend fun saveCallResult(item: List<Material>) = materialDoa.insertMaterials(item)

		}.asLiveData()

	}

	fun loadOrients() : LiveData<Resource<List<Orient>>> {
		return object :NetworkBoundResource<List<Orient>>(uiScope){

			override fun shouldFetch(data: List<Orient>?): Boolean = data.isNullOrEmpty()

			override suspend fun loadFromDb(): List<Orient> = orientDao.getOrients()

			override suspend fun createCall(): List<Orient> = webApi.getOrients()

			override suspend fun saveCallResult(item: List<Orient>) = orientDao.insertOrients(item)


		}.asLiveData()
	}

	fun saveSocial(): LiveData<Resource<Map<String,String>>> {
		return object : NetworkOnlyResource<Map<String,String>>(uiScope) {
			override suspend fun createCall(): Map<String, String>  = webApi.getSocial()
		}.asLiveData()
	}

	fun loadPdf(path:String): LiveData<FileResource<ByteArray>> {
		byteArray.value = FileResource.Loading()
		val file =File(fileDir,path)
		if (file.exists()){
			byteArray.value = FileResource.FromCache(file)
			return byteArray
		}
		if (connectivityManager.activeNetworkInfo?.isConnected != true){
			Log.e("no internet","exception.message")
			byteArray.value = FileResource.Error()
		}


		val storageRef = storage.reference
		val pathReference = storageRef.child(path)
		pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {

			byteArray.value = FileResource.FromNetwork(it,file)
		}.addOnFailureListener {
			Log.e("Exce",it.message)
			byteArray.value = FileResource.Error(R.string.not_found, R.drawable.ic_404)
			it.printStackTrace()
		}
		return byteArray
	}


	fun cancelJob() = viewModelJob.cancel()

	companion object{
		const val ONE_MEGABYTE: Long = 1024 * 1024 * 2
	}

}


