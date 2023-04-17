package com.example.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebaseauth.model.Note
import com.example.firebaseauth.model.User
import com.example.firebaseauth.ui.theme.FireBaseAuthTheme
import com.example.firebaseauth.ui.theme.Typography
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Paper.book("book").read<User>("User")
        val fireRepo = FireRepo()

        if (user == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else {
            auth(
                email = user.email,
                password = user.password,
                onFailure = {
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                },
                onSuccess = {
                    setContent {
                        val scaffoldState = rememberScaffoldState()
                        FireBaseAuthTheme {
                            // A surface container using the 'background' color from the theme
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
                                            fireRepo.addNote(
                                                Note("", user.email),
                                                onSuccess = {
                                                    startActivity(
                                                        Intent(
                                                            this,
                                                            NoteDetailActivity::class.java
                                                        ).apply {
                                                            putExtra("id", it.id)
                                                        })
                                                },
                                                onFailure = {
                                                    Toast.makeText(
                                                        this,
                                                        it.localizedMessage,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )

                                        }) {
                                        Icon(

                                            imageVector = Icons.Filled.Add,
                                            contentDescription = Icons.Filled.Add.name
                                        )
                                    }
                                }
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(it),
                                    color = MaterialTheme.colors.background
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(text = "Добро пожаловать")
                                        Text(text = user.email)
                                        Button(onClick = {
                                            signOut()
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    AuthActivity::class.java
                                                )
                                            )
                                        }) {
                                            Text(text = "выйти")
                                        }

                                        NotesList()

                                    }
                                }
                            }
                        }
                    }

                }
            )
        }

    }


}


@Composable
fun NotesList(
) {
    val context = LocalContext.current
    val notesViewModel: NotesViewModel = viewModel(
        factory = NotesViewModel.Companion.ViewModelFactory(
            FireRepo()
        )
    )
    val notesList = notesViewModel.notes.collectAsState(initial = null).value
    Column {
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            notesList?.forEach { n ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clickable {
                                startActivity(
                                    context,
                                    Intent(
                                        context,
                                        NoteDetailActivity::class.java
                                    ).apply {
                                        putExtra("id", n.id)
                                    }, null
                                )
                            },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        NoteCard(
                            Note(
                                note = n.data[Note::note.name].toString()
                            ),
                            onDeleteClick = {
                                notesViewModel.deleteNote(
                                    id = n.id,
                                    onSuccess = {

                                        Toast.makeText(
                                            context,
                                            "Успешное удаление",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            it.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            })
                    }
                }


            }
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onDeleteClick: () -> Unit,
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = note.note,
                style = Typography.caption
            )
            IconButton(onClick = { onDeleteClick.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = Icons.Filled.Delete.name
                )
            }
        }

    }
}

private fun signOut() {
    val firebase = FirebaseAuth.getInstance()
    firebase.signOut()
    Paper.book("book").delete("User")
}

private fun auth(
    email: String,
    password: String,
    onSuccess: (() -> Unit)? = null,
    onFailure: (() -> Unit)? = null,
) {
    val firebase = FirebaseAuth.getInstance()
    firebase.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            onSuccess?.invoke()
        }
        .addOnFailureListener {
            onFailure?.invoke()
        }
}

