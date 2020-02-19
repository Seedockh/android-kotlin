package com.example.todoherissepierre.tasklist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoherissepierre.network.Api
import kotlinx.coroutines.coroutineScope

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> = _taskList

    suspend fun refresh() {
        val tasksResponse = tasksWebService.getTasks()
        if (tasksResponse.isSuccessful) {
            val fetchedTasks = tasksResponse.body()
            _taskList.postValue(fetchedTasks)
        }
    }

    suspend fun updateTask(task: Task) {
        tasksWebService.updateTask(task)
        val editableList = _taskList.value.orEmpty().toMutableList()
        val position = editableList.indexOfFirst { task.id == it.id }
        editableList[position] = task
        _taskList.value = editableList
    }

    suspend fun addTask(task: Task) {
        tasksWebService.createTask(task)
        val addTaskList = _taskList.value.orEmpty().toMutableList()
        addTaskList[addTaskList.size] = task
        _taskList.value = addTaskList
    }

    suspend fun deleteTask(id: String) {
        val tasksResponse = tasksWebService.deleteTask(id)
        if (tasksResponse.isSuccessful) {
            val refreshTasks = tasksWebService.getTasks()
            if (refreshTasks.isSuccessful) {
                val fetchedTasks = refreshTasks.body()
                Log.i("Refreshed tasks => ", fetchedTasks.toString())
                _taskList.postValue(fetchedTasks)
            }
        } else {
            Log.i("There was a error deleting ${id}", tasksResponse.toString())
        }
    }
}