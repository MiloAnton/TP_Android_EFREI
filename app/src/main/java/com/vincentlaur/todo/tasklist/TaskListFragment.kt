package com.vincentlaur.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vincentlaur.todo.R
import com.vincentlaur.todo.form.Form

class TaskListFragment : Fragment() {
    private var taskList = listOf(
        Task(id = "id_1", title = "Tâche 1", description = "Description")
    )
    private val adapter = TaskListAdapter()

    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        taskList = taskList + task
        adapter.submitList(taskList)
    }
    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val newTask = result.data?.getSerializableExtra("task") as Task
        taskList = taskList.map { if (it.id == newTask.id) newTask else it }
        adapter.submitList(taskList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter.submitList(taskList)
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = this.adapter
        var button = view.findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        button.setOnClickListener(){
            val intent = Intent(context, Form::class.java)
            createTask.launch(intent)
            adapter.submitList(taskList)
        }
        adapter.onClickDelete = { task ->
            taskList = taskList - task
            adapter.submitList(taskList)
        }
        adapter.onClickEdit = { task ->
            val intent = Intent(context, Form::class.java)
            intent.putExtra("task", task)
            editTask.launch(intent)
        }
    }

}