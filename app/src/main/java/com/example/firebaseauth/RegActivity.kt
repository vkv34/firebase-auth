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

class RegActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FireBaseAuthTheme {
                var email by remember {
                    mutableStateOf("")
                }
                var password by remember {
                    mutableStateOf("")
                }
                val context = LocalContext.current
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .padding(18.dp),
                    color = MaterialTheme.colors.background
                ) {
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
                            onClick = {
                                reg(email, password, context) {
                                    startActivity(
                                        Intent(
                                            this@RegActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                }

                            }) {
                            Text(text = "Зарегистрироваться")
                        }
                    }
                }
            }
        }
    }
}

private fun reg(
    email: String,
    password: String,
    context: Context,
    onSuccess: (() -> Unit)? = null,
) {
    val firebase = FirebaseAuth.getInstance()
    if (email.isEmpty() || password.isEmpty()) {
        return
    }
    firebase.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            val user = firebase.currentUser
            Toast.makeText(context, "Успешная регистрация", Toast.LENGTH_SHORT).show()
            user?.let {
                it.email?.let { email ->
                    Paper.book("book").write("User", User(email, password))
                }
            }
            onSuccess?.invoke()
        }
        .addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

    Paper.book("book").write("User", User(email, password))
}

