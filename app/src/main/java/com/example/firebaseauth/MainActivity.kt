package com.example.firebaseauth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.example.firebaseauth.model.User
import com.example.firebaseauth.ui.theme.FireBaseAuthTheme
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Paper.book("book").read<User>("User")
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
                        FireBaseAuthTheme {
                            // A surface container using the 'background' color from the theme
                            Surface(
                                modifier = Modifier.fillMaxSize(),
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
                                        finish()
                                    }) {
                                        Text(text = "выйти")
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

