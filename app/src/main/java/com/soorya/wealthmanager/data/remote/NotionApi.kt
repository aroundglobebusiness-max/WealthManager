package com.soorya.wealthmanager.data.remote

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface NotionApi {

    @GET("databases/{database_id}")
    suspend fun getDatabase(
        @Path("database_id") databaseId: String
    ): Response<JsonObject>

    @POST("pages")
    suspend fun createPage(
        @Body body: JsonObject
    ): Response<JsonObject>

    @PATCH("pages/{page_id}")
    suspend fun updatePage(
        @Path("page_id") pageId: String,
        @Body body: JsonObject
    ): Response<JsonObject>

    companion object {
        const val BASE_URL = "https://api.notion.com/v1/"
        const val NOTION_VERSION = "2022-06-28"
    }
}
