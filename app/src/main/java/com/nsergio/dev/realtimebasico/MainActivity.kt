package com.nsergio.dev.realtimebasico

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.nsergio.dev.realtimebasico.data.FirebaseInstance
import com.nsergio.dev.realtimebasico.data.models.Todo
import com.nsergio.dev.realtimebasico.databinding.ActivityMainBinding
import com.nsergio.dev.realtimebasico.databinding.DialogNewTaskBinding
import com.nsergio.dev.realtimebasico.ui.TodoAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseInstance: FirebaseInstance

    private val todoAdapter: TodoAdapter by lazy {
        TodoAdapter(
            onDelete = { idFirebaseReference ->
                firebaseInstance.deleteItemFirebase(idFirebaseReference)
            },
            onCheckDone = { idFirebaseReference, newTodoValue ->
                firebaseInstance.updateStateTodo(idFirebaseReference, newTodoValue)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebase()
        initAdapter()
        //setupFirebaseListenerSingleString()
        setupFirebaseListenerObject()

    }

    private fun initAdapter() {
        binding.recyclerViewTodo.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }
    }

    private fun setupFirebaseListenerObject() {

        //on call or test this fucntion, remove or clear all firebaseDataBase information
        //to prevent errors or crash
/*        binding.fabNewTodo.setOnClickListener {
            firebaseInstance.writeObjectOnFirebase()
        }*/
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = getClearSnapShot(snapshot)
                val listTodoFromSnapshot = getTodoFromSnapShot(data)
                todoAdapter.updateItems(listTodoFromSnapshot) { position ->
                    //binding.recyclerViewTodo.smoothScrollToPosition(position)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Cancelado", Toast.LENGTH_LONG).show()
            }

        }
        firebaseInstance.setListener(postListener)

    }

    private fun getTodoFromSnapShot(pairObjects: List<Pair<String, Todo>>): List<Todo> {
        return pairObjects.map { it.second } // obtiene la lista de 'Todo', es 'nueva lista'
            //se fuciona con la lista anterior
            .zip(pairObjects) { task: Todo, firebaseObject: Pair<String, Todo> ->
                task.apply { putFirebaseReference(firebaseObject.first) }
            }
    }

    private fun getClearSnapShot(snapshot: DataSnapshot): List<Pair<String, Todo>> {
        /*
        * children son los "hijos-lista" que hay en la firebaseDatabase
        * */
        val list = snapshot.children.map {
            Pair(it.key!!, it.getValue<Todo>()!!)
        }
        return list
    }

    private fun setupFirebaseListenerSingleString() {
        //on call or test this fucntion, remove or clear all firebaseDataBase information
        //to prevent errors or crash
        binding.buttonUpdate.setOnClickListener {
            firebaseInstance.writeSingleStringOnFirebase("Mi primera escritura en Firebase")
        }
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue<String>()
                data?.let {
                    binding.textViewResult.text = it
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Cancelado", Toast.LENGTH_LONG).show()
            }

        }
        firebaseInstance.setListener(postListener)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_todo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.optionAddTodo -> {
                showDialogNewTask()
                //firebaseInstance.writeObjectOnFirebase()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showDialogNewTask() {
        val binding = DialogNewTaskBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        binding.addTaskDialogButton.setOnClickListener {
            val tile = binding.titleTaskTextView.text.toString()
            val description = binding.descriptionTaskTextView.text.toString()
            if (tile.isEmpty() or description.isEmpty()) {
                Toast.makeText(this, "Not empty values allowed", Toast.LENGTH_LONG).show()
            } else {
                firebaseInstance.createTask(
                    title = tile,
                    description = description,
                )
                dialog.dismiss()
            }
        }
        dialog.setContentView(binding.root)
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        dialog.show()
    }
    private fun initFirebase() {
        firebaseInstance = FirebaseInstance(this)
        firebaseInstance.iniFirebase()
    }

}