package com.vincentlaur.todo.tasklist

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.vincentlaur.todo.R
import com.vincentlaur.todo.form.Form
import com.vincentlaur.todo.network.Api
import com.vincentlaur.todo.user.UserInfoActivity
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {
    private val adapter = TaskListAdapter()
    private val viewModel: TasksListViewModel by viewModels()
    private lateinit var textUserName: TextView;
    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        viewModel.create(task)
    }
    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val newTask = result.data?.getSerializableExtra("task") as Task
        viewModel.update(newTask)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        var avatar = view?.findViewById<ImageView>(R.id.avatarImageView)
        avatar?.setOnClickListener{
            val intent = Intent(context, UserInfoActivity::class.java)
            startActivity(intent)
        }

        recyclerView.adapter = this.adapter
        var button = view.findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        var txtView = view.findViewById<TextView>(R.id.userName)
        this.textUserName = txtView;

        button.setOnClickListener(){
            val intent = Intent(context, Form::class.java)
            createTask.launch(intent)
        }
        adapter.onClickDelete = { task ->
            viewModel.delete(task)
        }
        adapter.onClickEdit = { task ->
            val intent = Intent(context, Form::class.java)
            intent.putExtra("task", task)
            editTask.launch(intent)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est executée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val tmp = this.textUserName
        var avatar = view?.findViewById<ImageView>(R.id.avatarImageView)
        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            tmp.text = "Bonjour " + userInfo.firstName + " " + userInfo.lastName
            avatar?.load(userInfo.avatar)
            viewModel.refresh()
        }
    }
}