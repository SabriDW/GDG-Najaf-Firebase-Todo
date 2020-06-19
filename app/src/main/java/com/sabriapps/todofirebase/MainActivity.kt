package com.sabriapps.todofirebase

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var adapter: FirestoreRecyclerAdapter<*, *>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val db = Firebase.firestore

        val collectionName = "todos-${FirebaseAuth.getInstance().currentUser?.uid}"

        logout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener {

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                }
        }


        todoAddButton.setOnClickListener {

            val todoText = todoEditText.text.toString()


            if (todoText.isNotBlank()) {
                db.collection(collectionName)
                    .add(
                        TodoItem(
                            todoEditText.text.toString(),
                            false
                        )
                    ).addOnSuccessListener {
                        todoEditText.setText("")
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.todo_added_successfully),
                            Toast.LENGTH_SHORT
                        ).show();
                    }
            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.please_enter_a_todo_item),
                    Toast.LENGTH_SHORT
                ).show();
            }

        }


        val query = db
            .collection(collectionName)


        val options = FirestoreRecyclerOptions.Builder<TodoItem>()
            .setQuery(query, TodoItem::class.java)
            .build()


        adapter =
            object : FirestoreRecyclerAdapter<TodoItem?, TodoViewHolder?>(options) {

                override fun onBindViewHolder(
                    holder: TodoViewHolder,
                    position: Int,
                    model: TodoItem
                ) {
                    holder.setData(model)
                }

                override fun onCreateViewHolder(group: ViewGroup, i: Int): TodoViewHolder {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.item_todo, group, false)
                    return TodoViewHolder(view)
                }

            }

        todoList.layoutManager = LinearLayoutManager(this)
        todoList.adapter = adapter

    }


    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val todoText: TextView
        val todoCheckBox: CheckBox
        val todoDeleteButton: ImageButton

        init {
            todoText = view.findViewById(R.id.todoTextView)
            todoCheckBox = view.findViewById(R.id.todoCheckbox)
            todoDeleteButton = view.findViewById(R.id.todoDeleteButton)


            todoCheckBox.setOnCheckedChangeListener { _, isChecked ->

                adapter?.snapshots?.getSnapshot(layoutPosition)
                    ?.reference?.update("done", isChecked)

            }

            todoDeleteButton.setOnClickListener {
                adapter?.snapshots?.getSnapshot(layoutPosition)
                    ?.reference?.delete()
            }
        }

        fun setData(todoItem: TodoItem) {

            todoText.setText(todoItem.text)
            todoCheckBox.isChecked = todoItem.done
        }
    }
}