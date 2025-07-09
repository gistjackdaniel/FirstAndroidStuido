// UserViewModel.kt (또는 ReservationViewModel.kt)
package com.example.daejeonpass.model
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.daejeonpass.customUi.profile.ReservationTrip
import com.example.daejeonpass.model.UserProfile // UserProfile 임포트
import kotlin.collections.any
import kotlin.collections.find




class ReservationViewModel(application: Application) : AndroidViewModel(application) {
    private val _reservations = mutableStateListOf<ReservationTrip>()
    val reservations: List<ReservationTrip> = _reservations


    // Post의 ID가 Int라고 가정
    fun addReservation(postId: Int, postTitle: String, postDate: String, user: UserProfile) {
        val existingReservation = _reservations.find { it.id == postId } // postId를 String으로 변환하여 비교
        if (existingReservation != null) {
            // 이미 해당 여행에 대한 예약이 있으면, 참여자만 추가
            if (!existingReservation.participants.any { it.name == user.name }) { // 이름 기반 중복 체크
                val updatedParticipants = existingReservation.participants + user
                // _reservations 리스트에서 해당 아이템을 직접 수정해야 Compose가 변경을 감지함
                val index = _reservations.indexOf(existingReservation)
                if (index != -1) {
                    _reservations[index] = existingReservation.copy(participants = updatedParticipants)
                }
            }
        } else {
            // 새로운 예약인 경우
            _reservations.add(0, ReservationTrip(postId, postTitle, postDate, listOf(user))) // postId를 String으로 변환하여 id로 사용
        }
    }
}

