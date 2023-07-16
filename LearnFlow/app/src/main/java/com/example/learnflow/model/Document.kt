package com.example.learnflow.model

import java.io.File

class Document(
    var uploadUrl: String,
    var documentType: DocumentType,
    var file: File
) {
    inner class DocumentType(
        var name: String,
        var description: String
    )
}