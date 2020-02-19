package com.heroup.bacexams.ui.exam

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.heroup.bacexams.models.Material
import com.heroup.bacexams.repository.Repository

class MainViewModel(private val repository: Repository) :ViewModel(){
	private val _path = MutableLiveData<String>()
	private val _orient = MutableLiveData<String>()



	fun getPdfBytes() = Transformations.switchMap(_path){
		Log.e("MainViewModel","Load PDF")
		repository.loadPdf(it)
	}

	var path
		get() = _path.value
		set(value) {
			if (value != _path.value){
				Log.e("MainViewModel","Path changed")
				_path.value = value
			}
		}

	var year :Int? = null

	var mode :String? = null

	lateinit var currentMaterial : Material

	fun resetExam(){
		year = null
		mode = null
	}

	var orient
		get() = _orient.value
		set(value) { _orient.value = value }

	fun getMaterials() = Transformations.switchMap(_orient){
		repository.loadMaterials(it)
	}

	fun retryMaterial() {
		orient?.let {
			repository.loadMaterials(it)
		}

	}


	fun getOrients() = repository.loadOrients()

	override fun onCleared() {
		super.onCleared()
		repository.cancelJob()


	}


}