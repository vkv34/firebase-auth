package com.example.firebaseauth

import com.example.firebaseauth.model.Note
import com.example.firebaseauth.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.paperdb.Paper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class FireRepo {

    private val db = FirebaseFirestore.getInstance()

    fun getNotes(user: User) = callbackFlow {

        val collection = db.collection("notes")
            .whereEqualTo("user", user.email)
        val snapshotListener = collection.addSnapshotListener { value, error ->
            trySend(value)
        }

        awaitClose {
            snapshotListener.remove()
        }
    }

    fun addNote(
        note: Note,
        onSuccess: ((DocumentReference) -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null,
    ) {
        val collection = db.collection("notes")

        collection.add(note)
            .addOnSuccessListener { onSuccess?.invoke(it) }
            .addOnFailureListener { onFailure?.invoke(it) }
    }

    fun addUser(
        user: User,
        onSuccess: ((DocumentReference) -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null,
    ) {
        val collection = db.collection("users")

        collection.add(user)
            .addOnSuccessListener { onSuccess?.invoke(it) }
            .addOnFailureListener { onFailure?.invoke(it) }
    }

    fun getNoteById(id: String, onSuccess: ((DocumentSnapshot) -> Unit)? = null) =
        db.collection("notes")
            .document(id)
            .get()
            .addOnSuccessListener{onSuccess?.invoke(it)}
    fun updateNote(
        id: String,
        note: Note,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null,
    ) =
        db.collection("notes")
            .document(id)
            .set(note)
            .addOnSuccessListener {
                onSuccess?.invoke()
            }
            .addOnFailureListener {
                onFailure?.invoke(it)
            }

    fun deleteNote(
        id: String,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null,
    ) =
        db.collection("notes")
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess?.invoke()
            }
            .addOnFailureListener {
                onFailure?.invoke(it)
            }

    companion object {

        val currentUser: User?
            get() = Paper.book("book").read<User>("User")
    }
}