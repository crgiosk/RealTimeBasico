package com.nsergio.dev.realtimebasico.data

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.nsergio.dev.realtimebasico.data.models.Todo
import kotlin.random.Random

class FirebaseInstance(private val context: Context) {

    /*
    * No olvidar  crear la base de datos en Firebase
    * vas a todos productos y ubscas RealTimeFirebase
    * Elegir el servidor donde se gfuarda tu base de datos dependiendo a tu publco objetivo
    * */

    private val database = Firebase.database

    private fun getDatabaseReference() = database.reference

    fun iniFirebase() {
        FirebaseApp.initializeApp(context)
    }

    fun writeSingleStringOnFirebase(message: String) {
        val myReference = getDatabaseReference()
        //Se reescribe el valor cada vez quie se hace un setValue
        myReference.setValue("$message -> ${Random.nextInt(0, 10)}")
    }

    fun setListener(postListener: ValueEventListener) {
        database.reference.addValueEventListener(postListener)
    }

    fun writeObjectOnFirebase() {
        val myReference = getDatabaseReference()
        //lastValue obtiene el valor anterior del padre, para no sobreescribirlo tal como se
        //hacia writeSingleStringOnFirebase
        //myReference.setValue(getDatabaseReference())
        val lastValue = myReference.push()
        lastValue.setValue(getGenericTodoTaskItem())

    }

    fun createTask(title: String, description: String) {
        ///ref.setValue("Mi primera escritura ${Random.nextInt(1..10)}")
        //.push crea un nuevo index para el siguiente elemento de la bd en firebase
        val lastValue = getDatabaseReference().push()
        lastValue.setValue(
            Todo(
                title = title,
                description = description
            )
        )
    }

    private fun getGenericTodoTaskItem(): Todo {
        return Todo(
            title = "Tarea numero ${Random.nextInt(0, 10)}",
            description = "Descripcion de tarea"
        )
    }

    fun deleteItemFirebase(idFirebaseReference: String) {
        val reference = getDatabaseReference()
        //reference.child(idFirebaseReference)
        /*
        * obtiene un valor de "todos-lista" en la reference
        * reference es como una lista?!!
        * */
        reference.child(idFirebaseReference).removeValue()
    }

    fun updateStateTodo(idFirebaseReference: String, newValue: Boolean) {
        //getDatabaseReference().child(idFirebaseReference).child("done").setValue(newValue)
        val taskFromFirebase = getDatabaseReference().child(idFirebaseReference)
        val taskObjectDone = taskFromFirebase.child("done")
        taskObjectDone.setValue(newValue)
    }
}