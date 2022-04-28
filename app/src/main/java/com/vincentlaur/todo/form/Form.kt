package com.vincentlaur.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.vincentlaur.todo.R
import com.vincentlaur.todo.tasklist.Task
import java.util.*

class Form : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val titleText = findViewById<EditText>(R.id.editTitle)
        val descriptionText = findViewById<EditText>(R.id.editDesc)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val taskEdit = intent.getSerializableExtra("task") as? Task
        descriptionText.setText(taskEdit?.description)
        titleText.setText(taskEdit?.title)
        buttonSave.setOnClickListener {
            val newTask = Task(id = taskEdit?.id ?: UUID.randomUUID().toString(),
                title = titleText.text.toString(),description = descriptionText.text.toString())
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}