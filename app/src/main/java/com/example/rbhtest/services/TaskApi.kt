package com.example.rbhtest.services

import android.util.Log
import com.example.rbhtest.data.TodosBody
import com.example.rbhtest.data.response.TodoResponse
import com.example.rbhtest.utils.ApiStatus
import retrofit2.HttpException
import java.io.IOException

object TaskApi {
  private const val TAG = "TaskApi"

  suspend fun getTask(
    status: String,
    limit: Int = 10,
    offset: Int = 0,
    sortBy: String = "createdAt",
    isAsc: Boolean = true
  ): TodoResponse {
    try {
      Log.e(TAG, "GET REQUEST -> getTask: $status")
      val response = RetrofitInstance.taskApi.getTasks(status, limit, offset, sortBy, isAsc)
      if (response.isSuccessful) {
        return TodoResponse(
          ApiStatus.SUCCESS,
          response.code(),
          response.body() ?: TodosBody(),
          response.headers()
        )
      }
      return TodoResponse(
        ApiStatus.FAIL,
        response.code(),
        null,
        response.headers(),
        response.message()
      )
    } catch (e: IOException) {
      Log.e(TAG, "IOException", e)
      return TodoResponse(ApiStatus.EXCEPTION, null, null, null, "IOException")
    } catch (e: HttpException) {
      Log.e(TAG, "HttpException", e)
      return TodoResponse(ApiStatus.EXCEPTION, null, null, null, "HttpException")
    }
  }
}