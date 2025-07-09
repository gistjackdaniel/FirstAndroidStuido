package com.example.daejeonpass.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import com.example.daejeonpass.model.UserProfile
import com.example.daejeonpass.model.TravelMatePost
import com.google.firebase.firestore.remote.Datastore
import java.util.prefs.Preferences

object PostRepository{
    private var currentId = 1
    private val _posts = mutableStateListOf<TravelMatePost>() // Post Repository 내부 전용 _posts

    val posts: List<TravelMatePost> get() = _posts // 외부에서 읽을 수 있는 posts 제공
    var dummyDataAdded = false

    fun createAndAddPost(post: TravelMatePost):TravelMatePost {
        val postWithId = post.copy(id = currentId)
        _posts.add(postWithId)
        currentId++
        return postWithId
    }

    fun replacePost(index: Int, post: TravelMatePost){
        _posts[index] = post
    }

    fun addParticipantToPost(postId: Int, participant: UserProfile) {
        val postIndex = _posts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val oldPost = _posts[postIndex]
            if (!oldPost.participants.any { it.name == participant.name } && oldPost.currentpeople < oldPost.totalpeople) {
                val updatedParticipants = oldPost.participants + participant
                val updatedPost = oldPost.copy(
                    participants = updatedParticipants,
                    currentpeople = oldPost.currentpeople + 1
                )
                _posts[postIndex] = updatedPost // 리스트를 직접 수정하여 Recomposition 유도 (Compose State 사용 시)
                Log.d("PostRepository", "Participant ${participant.name} added to post $postId. Current: ${updatedPost.currentpeople}")
            }
        }
    }

    // 북마크 상태 변경 함수 (예시)
    fun toggleBookmark(postId: Int) {
        val postIndex = _posts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val oldPost = _posts[postIndex]
            _posts[postIndex] = oldPost.copy(isBookmarked = !oldPost.isBookmarked)
        }
    }
}