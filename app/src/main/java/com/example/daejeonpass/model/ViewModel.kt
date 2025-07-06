// file: app/src/main/java/com/example/daejeonpass/model/CommentViewModel.kt
package com.example.daejeonpass.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daejeonpass.data.ReviewComment // 데이터 클래스 임포트
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {

    // 리뷰 상세 정보를 위한 StateFlow
    private val _reviewDetails = MutableStateFlow<ReviewComment?>(null)
    val reviewDetails: StateFlow<ReviewComment?> = _reviewDetails.asStateFlow()

    // 내부 데이터 저장소: Key: reviewId, Value: 해당 리뷰의 ReviewComment 목록
    private val _commentsData = mutableMapOf<Int, MutableList<ReviewComment>>()

    // 각 reviewId별 댓글 목록을 발행하는 StateFlow 관리 맵
    private val _reviewCommentFlows = mutableMapOf<Int, MutableStateFlow<List<ReviewComment>>>()

    init {
        // ViewModel 생성 시 더미 데이터 초기화 (예시)
        // 실제 앱에서는 네트워크 또는 로컬 DB에서 데이터를 로드합니다.
        val dummyReviewId1 = 1 // 예시 리뷰 ID
        _commentsData[dummyReviewId1] = mutableListOf(
            ReviewComment(reviewId = dummyReviewId1, authorName = "김투어", content = "대전 정말 멋져요! (더미)"),
            ReviewComment(reviewId = dummyReviewId1, authorName = "박여행", content = "성심당 꼭 가세요! (더미)")
        )
        // 필요한 경우 다른 리뷰 ID에 대한 더미 데이터도 추가
    }

    // ▼▼▼ "Unresolved reference" 에러를 해결하기 위해 이 함수 추가 ▼▼▼
    fun loadReviewData(reviewId: Int, imageResFromNav: Int) {
        viewModelScope.launch {
            // --- 리뷰 상세 정보 로드 (또는 더미 데이터 생성) ---
            // 실제 앱에서는 reviewId를 사용해 DB나 네트워크에서 데이터를 가져옵니다.
            // 여기서는 더미 ReviewComment 객체를 생성합니다.
            val fetchedDetails = ReviewComment(
                reviewId = reviewId,
                content = "이곳은 $reviewId 번째 리뷰의 내용입니다. 전달받은 이미지 리소스 ID는 $imageResFromNav 입니다. 아름다운 풍경과 맛있는 음식이 가득한 곳이었어요. 다음에 또 방문하고 싶습니다!",
                authorName = "작성자 $reviewId"
            )
            _reviewDetails.value = fetchedDetails

            // --- 댓글 데이터 로드 (또는 더미 데이터 생성) ---
            // 해당 reviewId에 대한 댓글이 이미 _commentsData에 없으면 더미 댓글을 생성하여 추가합니다.
            val currentComments = _commentsData.getOrPut(reviewId) {
                // 예시: reviewId에 따라 다른 더미 댓글 생성
                if (reviewId % 2 == 0) { // 짝수 ID
                    mutableListOf(
                        ReviewComment(reviewId = reviewId, authorName = "짝수리뷰 팬", content = "$reviewId 리뷰 아주 좋아요!"),
                        ReviewComment(reviewId = reviewId, authorName = "댓글러123", content = "이런 정보 감사합니다 ($reviewId).")
                    )
                } else { // 홀수 ID (dummyReviewId1 외의 홀수 ID)
                    mutableListOf(
                        ReviewComment(reviewId = reviewId, authorName = "홀수리뷰 방문객", content = "우와, $reviewId 번째 리뷰라니!"),
                        ReviewComment(reviewId = reviewId, authorName = "여행가고싶다", content = "$reviewId 정보 잘 봤습니다.")
                    )
                }
            }
            // 해당 reviewId의 댓글 Flow에 최신 댓글 목록을 발행합니다.
            // getOrPut을 사용하여 Flow가 없으면 새로 만들고, 있으면 기존 Flow를 사용합니다.
            _reviewCommentFlows.getOrPut(reviewId) { MutableStateFlow(emptyList()) }.emit(currentComments.toList())
        }
    }






    fun getCommentsFlow(reviewId: Int): StateFlow<List<ReviewComment>> {
        synchronized(_reviewCommentFlows) {
            return _reviewCommentFlows.getOrPut(reviewId) {
                val initialComments = _commentsData[reviewId]?.toList() ?: emptyList()
                MutableStateFlow(initialComments)
            }
        }
    }

    fun addCommentToReview(reviewId: Int, author: String, content: String) {
        val newComment = ReviewComment(
            reviewId = reviewId,
            authorName = author,
            content = content
        )

        val commentsList = synchronized(_commentsData) {
            _commentsData.getOrPut(reviewId) { mutableListOf() }.also {
                it.add(newComment) // 내부 데이터에 새 댓글 추가
            }
        }

        _reviewCommentFlows[reviewId]?.let { flow ->
            viewModelScope.launch {
                flow.emit(commentsList.toList()) // StateFlow에 업데이트된 불변 리스트 발행
            }
        }
    }
}