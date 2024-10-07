package com.example.todoapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomTaskAdapter(
    private val taskItems: MutableList<String>,
    private val onTaskEdit: (Int) -> Unit,
    private val onTaskDelete: (Int) -> Unit
) : RecyclerView.Adapter<CustomTaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskTitle.text = taskItems[position]

        holder.itemView.setOnClickListener {
            onTaskEdit(position)
        }

        holder.itemView.setOnLongClickListener {
            onTaskDelete(position)
            true
        }
    }

    override fun getItemCount(): Int = taskItems.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
    }
}
