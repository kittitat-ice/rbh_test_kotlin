package com.example.rbhtest.services

import com.example.rbhtest.data.TodosBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface TaskInterface {
  @GET("/todo-list")
  @Headers("Content-Type: application/json")
  suspend fun getTasks(
//    @Header("Authorization") authorization: String,
    @Query("status") status: String,
    @Query("limit") limit: Int,
    @Query("offset") offset: Int,
    @Query("sortBy") sortBy: String,
    @Query("isAsc") isAsc: Boolean,
  ): Response<TodosBody>

}
