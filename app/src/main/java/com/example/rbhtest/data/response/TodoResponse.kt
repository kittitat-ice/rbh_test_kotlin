package com.example.rbhtest.data.response

import com.example.rbhtest.data.TodosBody
import okhttp3.Headers

data class TodoResponse(
  val status: Int,
  val statusCode: Int? = null,
  val data: TodosBody? = null,
  val headers: Headers? = null,
  val message: String? = null
)
