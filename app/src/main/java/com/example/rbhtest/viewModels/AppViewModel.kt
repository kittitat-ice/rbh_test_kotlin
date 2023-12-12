package com.example.rbhtest.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rbhtest.data.Task
import com.example.rbhtest.data.response.TodoResponse
import com.example.rbhtest.services.TaskApi
import com.example.rbhtest.utils.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection

class AppViewModel : ViewModel() {

  private val TAG = "APP VIEW MODEL"

  private val _todoList: MutableLiveData<List<Task>> by lazy {
    MutableLiveData<List<Task>>(mutableListOf<Task>())
  }
  val todoList: LiveData<List<Task>> = _todoList

  private val _todoListResponse: MutableLiveData<TodoResponse> by lazy {
    MutableLiveData<TodoResponse>(TodoResponse(ApiStatus.NOT_STARTED))
  }
  val todoListResponse: LiveData<TodoResponse> = _todoListResponse

  private val SECRET_PIN = "123456"

  private val _inputPin: MutableLiveData<String> by lazy {
    MutableLiveData<String>("")
  }
  val inputPin: LiveData<String> = _inputPin

  fun isInputAtMax(): Boolean {
    return inputPin.value?.let { it.length >= 6 } ?: false
  }

  fun isInputCorrect(): Boolean {
    return inputPin.value?.let { it == SECRET_PIN } ?: false
  }
  fun addInputChar(char: Char) {
    var newInputPin = inputPin.value ?: ""
    newInputPin += char
    _inputPin.postValue(newInputPin)
  }
  fun removeLastInputPin() {
    val newInputPin = inputPin.value?.dropLast(1) ?: ""
    _inputPin.postValue(newInputPin)
  }
  fun clearInputPin() {
    _inputPin.postValue("")
  }

  fun resetTodoListResponse() {
    _todoListResponse.postValue(TodoResponse(ApiStatus.NOT_STARTED))
  }

  fun removeTask(index: Int) {
    todoList.value?.let {
      val newList = it.toMutableList()
      newList.removeAt(index)
      _todoList.postValue(newList)
    }
  }

  fun requestGetTask(status: String, limit: Int, offset: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      _todoListResponse.postValue(TodoResponse(ApiStatus.LOADING))
      val response = TaskApi.getTask(status, limit, offset)
      _todoListResponse.postValue(response)
      when (response.status) {
        ApiStatus.SUCCESS -> {
          Log.e(
            TAG,
            "REQUEST SUCCESS -> Status: ${response.statusCode} STATUS:${status} LIMIT:${limit} OFFSET:${offset}"
          )
          response.data?.let {
            Log.e(TAG, it.tasks.toString())
            _todoList.postValue(it.tasks)
          }
        }

        ApiStatus.FAIL -> {
          Log.e(
            TAG,
            "REQUEST FAILED -> Status: ${response.statusCode} STATUS:${status} LIMIT:${limit} OFFSET:${offset}"
          )
          if (
            response.statusCode == HttpURLConnection.HTTP_NOT_FOUND ||
            response.statusCode == HttpURLConnection.HTTP_NO_CONTENT
          ) {
            _todoList.postValue(listOf<Task>())
          }
        }

        ApiStatus.EXCEPTION -> Log.e(
          TAG,
          "REQUEST EXCEPTION FAILED -> TYPE: ${response.message} STATUS:${status} LIMIT:${limit} OFFSET:${offset}"
        )
      }
    }
  }
}