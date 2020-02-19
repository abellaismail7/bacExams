package com.heroup.bacexams.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.heroup.bacexams.models.Material

@Dao
interface MaterialDoa{

	@Insert
	suspend fun insertMaterials(material:List<Material>)

	@Query("SELECT * From Material WHERE orient = (:orient) ")
	suspend fun getMaterials(orient:String) :List<Material>
}