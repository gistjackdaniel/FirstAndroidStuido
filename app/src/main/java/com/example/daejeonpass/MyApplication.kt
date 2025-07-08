package com.example.daejeonpass

import android.app.Application
import com.example.daejeonpass.model.UserViewModel

class MyApplication: Application() {
    val userViewModel: UserViewModel by lazy { UserViewModel() }
}