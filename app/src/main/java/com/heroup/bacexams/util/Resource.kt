package com.heroup.bacexams.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heroup.bacexams.R

sealed class Resource<T> {
	data class Success<T>(val data : T): Resource<T>()
	data class Error<T>(@StringRes val msg: Int = R.string.no_internet, @DrawableRes val illustrationId:Int = R.drawable.ic_wifi): Resource<T>()
	class Loading<T> :Resource<T>()
}
