package com.example.todoherissepierre.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoherissepierre.R
import com.example.todoherissepierre.task.TaskActivity
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.item_task.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class TaskListFragment : Fragment() {
    private var taskList: MutableList<Task> = mutableListOf()

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
            taskList.remove(it)
            adapter.notifyDataSetChanged()
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