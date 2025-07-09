package com.example.daejeonpass.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(application) as T // 여기서 'application' 매개변수가 전달됨
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}