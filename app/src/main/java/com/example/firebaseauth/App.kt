package com.example.firebaseauth

import android.app.Application
import io.paperdb.Paper

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}