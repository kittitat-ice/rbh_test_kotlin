package com.example.rbhtest.services

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
  val taskApi: TaskInterface by lazy {
    Retrofit.Builder()
      .baseUrl("https://todo-list-api-mfchjooefq-as.a.run.app")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(TaskInterface::class.java)
  }
}