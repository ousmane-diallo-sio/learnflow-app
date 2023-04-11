package com.example.learnflow.components

import android.util.AttributeSet

interface IComponent {
    fun handleAttrs(attrs: AttributeSet?)
    fun setOnClickListeners()
}