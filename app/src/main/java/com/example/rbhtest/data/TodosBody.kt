package com.example.rbhtest.data

data class TodosBody(
  val tasks: List<Task> = listOf(),
  val pageNumber: Int = 0,
  val totalPages: Int = 0,
)