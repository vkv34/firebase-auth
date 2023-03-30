package com.example.firebaseauth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.firebaseauth.model.User
import com.example.firebaseauth.ui.theme.FireBaseAuthTheme
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper

class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FireBaseAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .padding(18.dp),
                    color = MaterialTheme.colors.background
                ) {
                    var email by remember {
                        mutableStateOf("")
                    }
                    var password by remember {
                        mutableStateOf("")
                    }
                    val context = LocalContext.current

                    FireBaseAuthTheme {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = email,
                                onValueChange = { email = it },
                                label = {
                                    Text(text = "Логин")
                                }
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = password,
                                onValueChange = { password = it },
                                label = {
                                    Text(text = "Пароль")
                                }
                            )


                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    auth(
                                        email, password,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Успешная авторизация",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Paper.book("book").write("User", User(email, password))
                                            startActivity(
                                                Intent(
                                                    this@AuthActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                            finish()
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                "НеУспешная авторизация",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }) {
                                Text(text = "Войти")
                            }

                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                startActivity(Intent(this@AuthActivity, RegActivity::class.java))
                            }) {
                                Text(text = "Зарегистрироваться")
                            }

                        }
                    }
                }
            }
        }
    }
}

private fun auth(
    email: String,
    password: String,
    onSuccess: (() -> Unit)?,
    onFailure: (() -> Unit)?,
) {
    val firebase = FirebaseAuth.getInstance()
    if (email.isEmpty() || password.isEmpty()) {
        onFailure?.invoke()
        return
    }
    firebase.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            onSuccess?.invoke()
        }
        .addOnFailureListener {
            onFailure?.invoke()
        }
}

private fun reg(email: String, password: String, context: Context) {
    val firebase = FirebaseAuth.getInstance()
    firebase.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            val user = firebase.currentUser
            Toast.makeText(context, "asfsdafsa", Toast.LENGTH_SHORT).show()

        }
        .addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

    Paper.book("book").write("User", User(email, password))
}