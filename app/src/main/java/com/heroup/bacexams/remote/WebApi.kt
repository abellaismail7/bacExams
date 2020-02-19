package com.heroup.bacexams.remote

import com.heroup.bacexams.models.Material
import com.heroup.bacexams.models.Orient
import retrofit2.http.GET
import retrofit2.http.Path

interface WebApi{
	@GET("/data/{orient}.json")
	suspend fun getMaterials(@Path("orient") slug: String): List<Material>

	@GET("/data/orients.json")
	suspend fun getOrients() : List<Orient>

	@GET("/data/social.json")
	suspend fun getSocial() : Map<String,String>
}