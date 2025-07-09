package com.example.daejeonpass.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ReservationViewModelFactory.kt (필요한 경우 생성)
class ReservationViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}