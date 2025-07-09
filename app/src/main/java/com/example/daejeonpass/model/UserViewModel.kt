package com.example.daejeonpass.model

import android.net.Uri
import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.add
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.daejeonpass.model.UserProfile
import kotlin.collections.any
import kotlin.collections.find
import kotlin.collections.remove


class UserViewModel: ViewModel(){
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _age = MutableStateFlow(0)
    val age: StateFlow<Int> = _age

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri

    fun setUserInfo(nickname: String, age: Int, gender: String, profileUri: Uri?) {
        _nickname.value = nickname
        _age.value = age
        _gender.value = gender
        _profileImageUri.value = profileUri
    }
}