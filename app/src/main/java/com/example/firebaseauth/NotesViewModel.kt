package com.example.firebaseauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebaseauth.model.User

class NotesViewModel(val fireRepo: FireRepo) : ViewModel() {

    val notes
        get() = fireRepo.getNotes(FireRepo.currentUser ?: User())

    fun deleteNote(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (ex: Exception) -> Unit,
    ) {
        try {
            fireRepo.deleteNote(id)
            onSuccess.invoke()
        } catch (ex: Exception) {
            onFailure(ex)
        }
    }

    companion object {
        class ViewModelFactory(private val fireRepo: FireRepo) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(fireRepo) as T
            }
        }
    }
}