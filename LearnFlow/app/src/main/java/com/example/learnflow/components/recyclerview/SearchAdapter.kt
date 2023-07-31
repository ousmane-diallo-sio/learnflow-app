package com.example.learnflow.components.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnflow.components.UserSearchItem
import com.example.learnflow.model.User

class SearchAdapter(
    private val items: MutableList<User>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(
            UserSearchItem(parent.context, null)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is UserViewHolder -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<User?>) {
        items.clear()
        items.addAll(newItems.filterNotNull())
        notifyDataSetChanged()
    }

}