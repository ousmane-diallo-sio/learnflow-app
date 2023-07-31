package com.example.learnflow.components.recyclerview

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learnflow.R
import com.example.learnflow.components.UserSearchItem
import com.example.learnflow.model.User

class UserViewHolder(itemView: UserSearchItem) : RecyclerView.ViewHolder(itemView) {
    fun bind(user: User) {
        val itemView = itemView as UserSearchItem

        itemView.tvName.text = "${user.firstName} ${user.lastName}"
        itemView.tvLocation.text = user.address.city
        try {
            val imageBytes = Base64.decode(user.profilePicture, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            itemView.ivProfilePic.setImageBitmap(decodedImage)
        } catch (e: Exception) {
            Log.e("UserViewHolder", "Error decoding image: ${e.message}")
            Glide.with(itemView.context)
                .load(R.drawable.ic_launcher_background)
                .into(itemView.ivProfilePic)
        }
    }
}