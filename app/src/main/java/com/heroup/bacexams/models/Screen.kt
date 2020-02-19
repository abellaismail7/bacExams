package com.heroup.bacexams.models

import androidx.annotation.DrawableRes


data class Screen(
		@DrawableRes val image:Int,
		val title:String,
		val description: String
)