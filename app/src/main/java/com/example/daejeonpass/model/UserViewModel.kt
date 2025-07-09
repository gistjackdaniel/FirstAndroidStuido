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

    private val _reservations = mutableStateListOf<ReservationTrip>()
    val reservations: List<ReservationTrip> = _reservations

    // 새로운 예약 추가 함수 (newParticipant 타입을 UserProfile로 변경)
    fun addReservation(tripId: String, title: String, date: String, newParticipant: UserProfile) {
        val existingReservation = _reservations.find { it.id == tripId }
        if (existingReservation != null) {
            // 이미 해당 여행에 대한 예약이 있으면, 참여자만 추가
            if (!existingReservation.participants.any { it.name == newParticipant.name }) { // 이름 기반 중복 체크
                val updatedParticipants = existingReservation.participants + newParticipant
                _reservations.remove(existingReservation)
                _reservations.add(0, existingReservation.copy(participants = updatedParticipants))
            }
        } else {
            // 새로운 예약인 경우
            _reservations.add(0, ReservationTrip(tripId, title, date, listOf(newParticipant)))
        }
    }






}