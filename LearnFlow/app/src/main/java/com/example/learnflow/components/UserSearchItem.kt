package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.learnflow.R

class UserSearchItem(context: Context, private val attrs: AttributeSet?) : LinearLayout(context, attrs) {

    val ivProfilePic: ImageView
    val tvName: TextView
    val tvLocation: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.user_search_item, this)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        ivProfilePic = findViewById(R.id.ivProfilePic)
        tvName = findViewById(R.id.tvName)
        tvLocation = findViewById(R.id.tvLocation)
    }

}