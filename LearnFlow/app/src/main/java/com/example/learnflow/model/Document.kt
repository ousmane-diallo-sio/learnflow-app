package com.example.learnflow.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Document(
    var name: String,
    var desc: String?,
    var base64: String,
    var documentType: String,
) : Parcelable {
    constructor(
        name: String,
        desc: String?,
        base64: String,
        documentType: DocumentType
    ) : this(name, desc, base64, documentType.toString())
}

enum class DocumentType {
    IMAGE,
    PDF;

    override fun toString(): String {
        return when (this) {
            IMAGE -> "image"
            PDF -> "pdf"
        }
    }
}