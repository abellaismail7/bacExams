package com.heroup.bacexams.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Material(
		@PrimaryKey override val id:Int,
		val orient: String,
		override val name: String,
		override val shortName:String,
		val years: List<Int>,
		override val icon:String,
		override val color:String) :Item



