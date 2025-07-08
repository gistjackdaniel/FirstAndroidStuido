package com.example.daejeonpass.data

import android.content.Context
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

    fun addParticipant(postId: Int, participant: UserProfile){
        //ID가 PostId인 게시물에 Participant를 참여자로 추가
        val index = _posts.indexOfFirst { it.id == postId }
        // index => ID가 PostId인 게시물이 List내 몇번째에 있는가
        if(index >= 0 ) {
            val post = _posts[index]
            if (post.participants.size < post.totalpeople) {
                _posts[index] = post.copy(
                    participants = post.participants + participant,
                    currentpeople = post.currentpeople + 1
                )
            }
        }
     }

}