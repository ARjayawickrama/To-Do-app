package com.example.todoapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskScreenActivity : AppCompatActivity() {

    private lateinit var taskAdapter: CustomTaskAdapter
    private lateinit var taskList: MutableList<String>
    private val taskPrefs = "TASK_DATA"
    private lateinit var taskInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        taskList = fetchTasks()

        taskAdapter = CustomTaskAdapter(taskList, ::updateTask, ::removeTask)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        taskInput = findViewById(R.id.taskInput)

        val newTaskButton: Button = findViewById(R.id.addTaskButton)
        newTaskButton.setOnClickListener {
            val taskText = taskInput.text.toString()
            if (taskText.isNotEmpty()) {
                taskList.add(taskText)
                taskAdapter.notifyDataSetChanged()
                storeTasks()
                taskInput.text.clear()
            }
        }
    }

    private fun fetchTasks(): MutableList<String> {
        val sharedPref = getSharedPreferences(taskPrefs, Context.MODE_PRIVATE)
        return sharedPref.getStringSet("tasks", setOf())?.toMutableList() ?: mutableListOf()
    }

    private fun storeTasks() {
        val sharedPref = getSharedPreferences(taskPrefs, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet("tasks", taskList.toSet())
            apply()
        }
    }

    private fun updateTask(position: Int) {
        val taskInput = EditText(this)
        taskInput.setText(taskList[position])

        AlertDialog.Builder(this)
            .setTitle("Modify Task")
            .setView(taskInput)
            .setPositiveButton("Update") { _, _ ->
                val updatedTask = taskInput.text.toString()
                if (updatedTask.isNotEmpty()) {
                    taskList[position] = updatedTask
                    taskAdapter.notifyDataSetChanged()
                    storeTasks()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun removeTask(position: Int) {
        taskList.removeAt(position)
        taskAdapter.notifyDataSetChanged()
        storeTasks()
    }
}
