package com.heroup.bacexams.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.heroup.bacexams.models.Orient

@Dao
interface OrientDao{

	@Insert
	suspend fun insertOrients( material:List<Orient> )

	@Query("SELECT * From orient")
	suspend fun getOrients() :List<Orient>

}