package com.heroup.bacexams.ui.intro

import androidx.lifecycle.ViewModel
import com.heroup.bacexams.repository.Repository

class IntroViewModel(private val repository: Repository) :ViewModel(){

	fun getOrients() = repository.loadOrients()
	fun saveSocial() = repository.saveSocial()

	override fun onCleared() {
		super.onCleared()
		repository.cancelJob()
	}
}