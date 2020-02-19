package com.heroup.bacexams.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Orient(
		@PrimaryKey override val id:Int,
		override val name: String,
		 override val shortName:String,
		override val icon:String,
		override val color:String):Item