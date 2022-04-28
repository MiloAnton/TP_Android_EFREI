package com.vincentlaur.todo.tasklist

data class Task(var id : String, var title : String, var description : String = "") : java.io.Serializable
