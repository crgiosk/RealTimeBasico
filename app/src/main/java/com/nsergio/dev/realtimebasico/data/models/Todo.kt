package com.nsergio.dev.realtimebasico.data.models

data class Todo(
    //este id es la "referencia" en firebase
    var id: String? = "",
    val title: String? = "",
    val description: String? = "",
    val done: Boolean? = false
) {
    fun putFirebaseReference(idReferences: String) {
        id = idReferences
    }
}