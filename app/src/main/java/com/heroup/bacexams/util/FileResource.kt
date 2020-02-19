package com.heroup.bacexams.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.heroup.bacexams.R
import java.io.File

sealed class FileResource<T> {
	class FromNetwork<T>(val byteArray: ByteArray,val file: File): FileResource<T>()
	data class FromCache<T>(val file : File): FileResource<T>()
	data class Error<T>(@StringRes val msg: Int = R.string.no_internet, @DrawableRes val illustrationId:Int = R.drawable.ic_wifi): FileResource<T>()
	class Loading<T> :FileResource<T>()
}
