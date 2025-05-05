package pion.tech.pionbase.home.data.api

import pion.tech.pionbase.home.data.model.ApiObjectResponseEntity
import pion.tech.pionbase.home.data.model.AppCategoryEntity
import pion.tech.pionbase.home.data.model.template.TemplateModelEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("api/v5.0/public/categories?app_id=56ba3e1f-27a4-4acd-b420-1b33600ac495")
    suspend fun getAppCategory(): ApiObjectResponseEntity<List<AppCategoryEntity>>

    @GET("api/v5.0/public/items/get-all?region_code=%7Bregion_code%7D")
    suspend fun getAllTemplate(@Query("category_id") categoryId: String): ApiObjectResponseEntity<List<TemplateModelEntity>>
}