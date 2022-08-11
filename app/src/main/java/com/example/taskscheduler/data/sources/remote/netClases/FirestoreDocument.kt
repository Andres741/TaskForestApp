package com.example.taskscheduler.data.sources.remote.netClases

import com.google.firebase.firestore.CollectionReference

interface IFirestoreDocument {
    fun obtainDocumentName(): String
}

@JvmInline
value class SimpleFirestoreDocument(
    private val docName: String
): IFirestoreDocument {
    override fun obtainDocumentName() = docName
}

fun CollectionReference.setDoc(doc: IFirestoreDocument) = document(doc.obtainDocumentName()).set(doc)

fun CollectionReference.deleteDoc(doc: IFirestoreDocument) = document(doc.obtainDocumentName()).delete()
