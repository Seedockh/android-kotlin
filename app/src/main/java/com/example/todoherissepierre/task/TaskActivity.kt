package com.example.todoherissepierre.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todoherissepierre.R
import com.example.todoherissepierre.tasklist.Task
import kotlinx.android.synthetic.main.activity_task.*
import java.io.Serializable
import java.util.*

class TaskActivity : AppCompatActivity() {
    companion object {
        const val TASK_KEY = "New task"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        when(intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { task_description_text.setText(it) }
                }
            } else -> {
                val editableTask = intent.getSerializableExtra(TASK_KEY) as? Task
                task_title_text.setText(editableTask?.title)
                task_description_text.setText(editableTask?.description)

                // Add complete task
                create_task_button.setOnClickListener {
                    val newTask = Task(
                        id = editableTask?.id ?: UUID.randomUUID().toString(),
                        title = task_title_text.text.toString(),
                        description = task_description_text.text.toString()
                    )
                    intent.putExtra(TASK_KEY, newTask as Serializable)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

}
