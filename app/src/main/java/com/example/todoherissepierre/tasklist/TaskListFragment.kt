package com.example.todoherissepierre.tasklist

import android.content.Intent
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoherissepierre.R
import com.example.todoherissepierre.network.Api
import com.example.todoherissepierre.task.TaskActivity
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.item_task.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class TaskListFragment : Fragment() {
    private val tasksRepository = TasksRepository()
    private var taskList: MutableList<Task> = mutableListOf()
    private val coroutineScope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val instance = savedInstanceState?.getParcelableArrayList<Task>("TaskList")
        if (instance !== null) { taskList = instance.toMutableList() }

        return inflater.inflate(R.layout.fragment_task_list, container)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("TaskList", ArrayList(taskList))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_recycler_view.layoutManager = LinearLayoutManager(activity)
        val adapter = TaskListAdapter(taskList)
        fragment_recycler_view.adapter = adapter

        add_task_button.setOnClickListener {
            val intent = Intent(view.context, TaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        adapter.onEditClickListener = { task ->
            val editTaskIntent = Intent(view.context, TaskActivity::class.java)
            editTaskIntent.putExtra(TaskActivity.TASK_KEY, task as Serializable)
            startActivityForResult(editTaskIntent, EDIT_TASK_REQUEST_CODE)
        }

        adapter.onDeleteClickListener = {
            lifecycleScope.launch {
                Log.i("ID to delete => ", it.id)
                tasksRepository.deleteTask(it.id)
                taskList.remove(it)
                coroutineScope.launch { tasksRepository.refresh() }
                adapter.notifyDataSetChanged()
            }
        }

        // Ici on ne va pas gérer les cas d'erreur donc on force le crash avec "!!"
        coroutineScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            val suspendTask = Api.tasksWebService.getTasks().body()!!

            list_title.text = "${userInfo.firstName} ${userInfo.lastName}"
            taskList = suspendTask.toMutableList()
            adapter.notifyDataSetChanged()
        }

        tasksRepository.taskList.observe(this, androidx.lifecycle.Observer {
            taskList.clear()
            taskList.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            tasksRepository.refresh()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task = data!!.getSerializableExtra(TaskActivity.TASK_KEY) as Task

        when(requestCode) {
            ADD_TASK_REQUEST_CODE -> {
                taskList.add(task)
                fragment_recycler_view.adapter?.notifyItemInserted(taskList.size)
            }

            EDIT_TASK_REQUEST_CODE -> {
                val i = taskList.indexOfFirst { it.id == task.id }
                taskList[0] = task
                fragment_recycler_view.adapter?.notifyItemChanged(i)
            }
        }
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 100
        const val EDIT_TASK_REQUEST_CODE = 200
    }
}