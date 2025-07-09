package com.example.daejeonpass

import android.app.Application
import com.example.daejeonpass.model.ReservationViewModel
import com.example.daejeonpass.model.UserViewModel
import com.example.daejeonpass.model.ReviewViewModel
import com.example.daejeonpass.model.ReviewViewModelFactory
import com.example.daejeonpass.model.ReservationViewModelFactory


class MyApplication: Application() {
    val userViewModel: UserViewModel by lazy { UserViewModel() }

    // ReviewViewModel을 Application 스코프에서 관리
    val reviewViewModel: ReviewViewModel by lazy {
        // ReviewViewModelFactory를 사용하여 Application 인스턴스 전달
        ReviewViewModelFactory(this).create(ReviewViewModel::class.java)
    }


    val reservationViewModel: ReservationViewModel by lazy {
        // 만약 Factory를 만들었다면:
         ReservationViewModelFactory(this).create(ReservationViewModel::class.java)
    }


    override fun onCreate() {
        super.onCreate()
        // 필요한 경우 초기화 코드
    }















}