package com.heroup.bacexams.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heroup.bacexams.models.Material
import com.heroup.bacexams.models.Orient


@Database(entities = [Orient::class,Material::class],version = 1)
@TypeConverters(ListConverter::class)
abstract class AppDatabase: RoomDatabase() {
	abstract fun materialDoa() : MaterialDoa
	abstract fun orientDao() : OrientDao
}

class ListConverter {
	@TypeConverter
	fun fromString(json: String): List<Int> {
		val gson = Gson()
		val type = object : TypeToken<List<Int>>() {

		}.type
		return gson.fromJson(json, type)
	}

	@TypeConverter
	fun fromList(list: List<Int>): String {
		val gson = Gson()
		val type = object : TypeToken<List<Int>>() {

		}.type
		return gson.toJson(list, type)
	}
}