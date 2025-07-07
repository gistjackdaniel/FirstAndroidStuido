// file: app/src/main/java/com/example/daejeonpass/model/CommentViewModel.kt
package com.example.daejeonpass.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daejeonpass.R
import com.example.daejeonpass.data.ReviewComment // 데이터 클래스 임포트
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 리뷰 상세 정보를 위한 데이터 클래스 (새로 추가)
data class ReviewDetails(
    val id: Int,
    val title: String,
    val content: String,
    val authorName: String,
    val profileImageRes: Int, // 프로필 이미지 리소스 ID
    val reviewImageRes: Int,  // 리뷰 대표 이미지 리소스 ID
    val date: String,
    val rating: Float
)

class CommentViewModel : ViewModel() {

    // 리뷰 상세 정보를 위한 StateFlow
    private val _reviewDetails = MutableStateFlow<ReviewDetails?>(null)
    val reviewDetails: StateFlow<ReviewDetails?> = _reviewDetails.asStateFlow()

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

        // 초기 댓글 Flow 발행 (해당 ID로 접근 시 즉시 댓글 보이도록)
        _reviewCommentFlows[dummyReviewId1] = MutableStateFlow(
            _commentsData[dummyReviewId1]?.toList() ?: emptyList()
        )



    }

    // 리뷰 상세 정보와 댓글을 로드하는 함수
    fun loadReviewData(reviewId: Int, imageResFromNav: Int) {
        viewModelScope.launch {
            // --- 리뷰 상세 정보 로드 (또는 더미 데이터 생성) ---
            // 이미 로드된 정보가 있고, 요청된 reviewId와 같다면 다시 로드하지 않음 (선택적 최적화)
            if (_reviewDetails.value?.id == reviewId) {
                // 댓글 데이터는 reviewId가 같아도 항상 최신으로 갱신하거나,
                // 또는 댓글도 이미 로드되었다면 그대로 둘 수 있음.
                // 여기서는 댓글은 getCommentsFlow를 통해 이미 관리되므로 추가 작업 불필요.
            } else {
                // 실제 앱에서는 reviewId를 사용해 DB나 네트워크에서 데이터를 가져옵니다.
                // 여기서는 더미 ReviewDetails 객체를 생성합니다.
                val fetchedDetails = ReviewDetails(
                    id = reviewId,
                    title = "대전 여행 후기 ($reviewId)",
                    content = "이곳은 $reviewId 번째 리뷰의 상세 내용입니다. 전달받은 대표 이미지 리소스 ID는 $imageResFromNav 입니다. 아름다운 풍경과 맛있는 음식이 가득한 곳이었어요. 다음에 또 방문하고 싶습니다! 이 내용은 ViewModel에서 생성된 더미 데이터입니다.",
                    authorName = "여행가 $reviewId",
                    profileImageRes = R.drawable.profile1, // 예시 프로필 이미지
                    reviewImageRes = imageResFromNav, // Gallery에서 전달받은 이미지
                    date = "2024-07-26",
                    rating = (reviewId % 5).toFloat() // 0.0f ~ 4.0f 사이의 더미 별점
                )
                _reviewDetails.value = fetchedDetails
            }

            // --- 댓글 데이터 로드 (또는 더미 데이터 생성) ---
            // 해당 reviewId에 대한 댓글이 이미 _commentsData에 없으면 더미 댓글을 생성하여 추가합니다.
            // 그리고 해당 StateFlow에 발행합니다.
            // getCommentsFlow 함수가 이 역할을 이미 하고 있으므로, 여기서는 호출만으로 충분할 수 있습니다.
            // 다만, 최초 로드 시 댓글이 없다면 더미 댓글을 추가하는 로직은 여기에 두는 것이 명확합니다.
            if (!_commentsData.containsKey(reviewId)) {
                val dummyComments = if (reviewId % 2 == 0) { // 짝수 ID
                    mutableListOf(
                        ReviewComment(reviewId = reviewId, authorName = "짝수리뷰 팬", content = "$reviewId 리뷰 아주 좋아요! (ViewModel 생성)"),
                        ReviewComment(reviewId = reviewId, authorName = "댓글러123", content = "이런 정보 감사합니다 ($reviewId). (ViewModel 생성)")
                    )
                } else { // 홀수 ID
                    mutableListOf(
                        ReviewComment(reviewId = reviewId, authorName = "홀수리뷰 방문객", content = "우와, $reviewId 번째 리뷰라니! (ViewModel 생성)"),
                        ReviewComment(reviewId = reviewId, authorName = "여행가고싶다", content = "$reviewId 정보 잘 봤습니다. (ViewModel 생성)")
                    )
                }
                _commentsData[reviewId] = dummyComments
            }
            // 해당 reviewId의 댓글 Flow에 최신 댓글 목록을 발행합니다.
            // getOrPut을 사용하여 Flow가 없으면 새로 만들고, 있으면 기존 Flow를 사용합니다.
            _reviewCommentFlows.getOrPut(reviewId) { MutableStateFlow(emptyList()) }
                .emit(_commentsData[reviewId]?.toList() ?: emptyList())
        }
    }





    // 댓글 목록을 위한 StateFlow를 반환하는 함수
    fun getCommentsFlow(reviewId: Int): StateFlow<List<ReviewComment>> {
        synchronized(_reviewCommentFlows) { // 동시 접근 제어
            return _reviewCommentFlows.getOrPut(reviewId) {
                // ViewModel 초기화 시 또는 loadReviewData에서 _commentsData가 채워질 수 있음
                val initialComments = _commentsData[reviewId]?.toList() ?: emptyList()
                MutableStateFlow(initialComments)
            }
        }
    }

    // 댓글 추가 함수
    fun addCommentToReview(reviewId: Int, author: String, content: String) {
        val newComment = ReviewComment(
            reviewId = reviewId,
            authorName = author,
            content = content
            // timestamp는 ReviewComment 데이터 클래스에서 기본값으로 현재 시간 설정
        )

        val commentsList = synchronized(_commentsData) { // 동시 접근 제어
            _commentsData.getOrPut(reviewId) { mutableListOf() }.also {
                it.add(0, newComment) // 새 댓글을 목록 맨 위에 추가 (최신 댓글이 위로)
            }
        }

        // 해당 reviewId의 StateFlow에 업데이트된 댓글 목록 발행
        _reviewCommentFlows[reviewId]?.let { flow ->
            viewModelScope.launch {
                flow.emit(commentsList.toList()) // StateFlow에 업데이트된 불변 리스트 발행
            }
        }
    }
}