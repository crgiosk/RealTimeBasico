package com.nsergio.dev.realtimebasico.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nsergio.dev.realtimebasico.data.models.Todo
import com.nsergio.dev.realtimebasico.databinding.ItemTodoBinding

class TodoAdapter(
    private val onCheckDone: (idReference: String, newValue: Boolean) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private val items: MutableList<Todo> = mutableListOf()

    fun updateItems(newItems: List<Todo>, shouldScrollTo: (Int) -> Unit) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()

/*        var positionToScroll = -1
        items.diffAndAddObject(newItems).run {
            if (this.isNotEmpty()) {
                this.forEach { item ->
                    items.add(item)
                    positionToScroll = items.size - 1
                    notifyItemInserted(positionToScroll) // Notifica la posición del último elemento insertado
                }
            } else {
                positionToScroll = items.count()
                notifyItemRangeChanged(0, positionToScroll)
            }
        }
        if (positionToScroll >= 0) shouldScrollTo.invoke(positionToScroll)*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemTodoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }


    inner class ViewHolder(private val myView: ItemTodoBinding) :
        RecyclerView.ViewHolder(myView.root) {
        fun bind(todo: Todo) {
            myView.todo = todo
            myView.root.setOnClickListener {
                onDelete.invoke(todo.id ?: "")
                notifyItemRemoved(adapterPosition)
            }
            myView.checkBoxIsDone.setOnClickListener {
                //onCheckDone.invoke(!(todo.done ?: false))
                val newDoneValue = todo.done ?: false
                onCheckDone.invoke(todo.id ?: "", !newDoneValue)
            }
        }
    }
}