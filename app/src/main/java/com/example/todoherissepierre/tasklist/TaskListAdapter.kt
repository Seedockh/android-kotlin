package com.example.todoherissepierre.tasklist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoherissepierre.R
import kotlinx.android.synthetic.main.item_task.view.*

class TaskListAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            // C'est ici qu'on reliera les données et les listeners une fois
            // l'adapteur implémenté
            itemView.task_title.text = task.title
            itemView.task_description.text = task.description

            itemView.edit_task_button.setOnClickListener { onEditClickListener.invoke(task) }
            itemView.delete_task_button.setOnClickListener { onDeleteClickListener.invoke(task) }
            itemView.setOnLongClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${task.title}, ${task.description}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, "Share with ")
                it.context.startActivity(shareIntent)
                true
            }
        }
    }

    override fun getItemCount(): Int = taskList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) = holder.bind(taskList[position])

    // Déclaration d'une lambda pour l'édition:
    var onEditClickListener: (Task) -> Unit = { task -> /* faire qqchose */ }
    // Déclaration d'une lambda pour la suppression:
    var onDeleteClickListener: (Task) -> Unit = { task -> /* faire qqchose */ }
}