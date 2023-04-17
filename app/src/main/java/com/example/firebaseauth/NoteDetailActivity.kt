package com.example.firebaseauth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.firebaseauth.model.Note
import com.example.firebaseauth.ui.theme.FireBaseAuthTheme

class NoteDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.extras?.getString("id") ?: run {
            Toast.makeText(this, "Заметка не найдена", Toast.LENGTH_SHORT).show()
            finish()
            ""
        }
        val fireRepo = FireRepo()
        setContent {
            var note by remember {
                mutableStateOf("")
            }
            LaunchedEffect(true) {
                fireRepo.getNoteById(
                    id,
                    onSuccess = {
                        note = it.data?.get("note").toString()
                    }
                )
            }

            FireBaseAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val scaffoldState = rememberScaffoldState()
                    val context = LocalContext.current
                    Scaffold(
                        scaffoldState = scaffoldState,
                        floatingActionButton = {
                            IconButton(
                                modifier = Modifier.background(
                                    MaterialTheme.colors.onBackground.copy(
                                        alpha = 0.3f
                                    ),
                                    shape = RoundedCornerShape(50)
                                ),
                                onClick = {
                                    try {
                                        fireRepo.deleteNote(id)
                                        Toast.makeText(
                                            context,
                                            "Успешное удаление",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (ex: Exception) {
                                        Toast.makeText(
                                            context,
                                            ex.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    finish()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = Icons.Filled.Delete.name
                                )
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(it)) {
                            TextField(value = note, onValueChange = { note = it })
                            Button(onClick = {
                                fireRepo.updateNote(
                                    id,
                                    Note(
                                        note,
                                        FireRepo.currentUser?.email ?: ""
                                    ),
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Изменения сохранены",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        finish()

                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(
                                            context,
                                            exception.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }) {
                                Text(text = "Сохранить изменения")
                            }
                        }
                    }
                }
            }
        }
    }
}

