package com.example.learnflow.components

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.learnflow.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class TeacherSignupConfirmation : BottomSheetDialogFragment() {

    private var listener: TeacherSignupConfirmationListener? = null
    private lateinit var btn: CustomBtn

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.teacher_signup_success, container, false)

        btn = v.findViewById(R.id.btnBackToLogin)

        btn.setOnClickListener {
            dismiss()
        }

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as TeacherSignupConfirmationListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement TeacherSignupConfirmationListener"
            )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onTeacherSignupConfirmationDismissed()
    }
}

interface TeacherSignupConfirmationListener {
    fun onTeacherSignupConfirmationDismissed()
}