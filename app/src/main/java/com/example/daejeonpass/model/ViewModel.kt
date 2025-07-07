// file: app/src/main/java/com/example/daejeonpass/model/CommentViewModel.kt
package com.example.daejeonpass.model

import androidx.compose.runtime.mutableStateListOf // SnapshotStateList 사용을 위해 추가
import androidx.compose.runtime.mutableStateMapOf // mutableStateMapOf 사용을 위해 추가
import androidx.compose.runtime.snapshots.SnapshotStateList // 타입 명시를 위해 추가
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

    // --- 댓글 데이터 관리 방식 변경 ---
    // Key: reviewId, Value: 해당 리뷰의 댓글 목록 (SnapshotStateList)
    private val _commentsMap = mutableStateMapOf<Int, SnapshotStateList<ReviewComment>>()
    // 외부에 노출 시에는 불변 Map으로 노출할 수 있으나, Composable에서 직접 사용 시 SnapshotStateMap 자체를 사용해도 됨
    // 여기서는 Composable에서 reviewId로 직접 접근하여 SnapshotStateList를 사용하도록 유도
    fun getCommentsForReview(reviewId: Int): SnapshotStateList<ReviewComment> {
        return _commentsMap.getOrPut(reviewId) {
            mutableStateListOf() // 해당 reviewId에 대한 리스트가 없으면 새로 생성
        }
    }

    // 내부 데이터 저장소: Key: reviewId, Value: 해당 리뷰의 ReviewComment 목록
    private val _commentsData = mutableMapOf<Int, MutableList<ReviewComment>>()

    // 각 reviewId별 댓글 목록을 발행하는 StateFlow 관리 맵
    private val _reviewCommentFlows = mutableMapOf<Int, MutableStateFlow<List<ReviewComment>>>()

    init {
        // ViewModel 생성 시 더미 데이터 초기화 (예시)
        // 실제 앱에서는 네트워크 또는 로컬 DB에서 데이터를 로드합니다.
        val dummyReviewIdForInitialComments = 1
        val initialCommentsList = getCommentsForReview(dummyReviewIdForInitialComments)
        if (initialCommentsList.isEmpty()) { // 중복 추가 방지
            initialCommentsList.addAll(
                listOf(
                    ReviewComment(reviewId = dummyReviewIdForInitialComments, authorName = "김투어", content = "대전 정말 멋져요! (초기 댓글)"),
                    ReviewComment(reviewId = dummyReviewIdForInitialComments, authorName = "박여행", content = "성심당 꼭 가세요! (초기 댓글)")
                )
            )
        }
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
                    content = "이곳은 $reviewId 번째 리뷰의 상세 내용입니다.",
                    authorName = "여행가 $reviewId",
                    profileImageRes = R.drawable.profile1, // 예시 프로필 이미지
                    reviewImageRes = imageResFromNav, // Gallery에서 전달받은 이미지
                    date = "2024-07-26",
                    rating = (reviewId % 6).toFloat() // 0.0f ~ 5.0f 사이의 더미 별점
                )
                _reviewDetails.value = fetchedDetails
            }

            // --- 댓글 데이터 로드 (또는 더미 데이터 생성) ---
            // 해당 reviewId에 대한 댓글이 이미 _commentsData에 없으면 더미 댓글을 생성하여 추가합니다.
            // 그리고 해당 StateFlow에 발행합니다.
            // getCommentsFlow 함수가 이 역할을 이미 하고 있으므로, 여기서는 호출만으로 충분할 수 있습니다.
            // 다만, 최초 로드 시 댓글이 없다면 더미 댓글을 추가하는 로직은 여기에 두는 것이 명확합니다.
            val commentsList = getCommentsForReview(reviewId)
            if (commentsList.isEmpty() && reviewId != 1 /* init에서 이미 처리한 ID 제외 */) {
                // 이전에 로드된 적 없는 reviewId에 대해서만 더미 댓글 추가 (init과 중복 방지)
                val dummyComments = if (reviewId % 2 == 0) {
                    listOf(
                        ReviewComment(reviewId = reviewId, authorName = "짝수리뷰 팬", content = "${reviewId}번째 동행 후기도 아주 좋아요! "),
                        ReviewComment(reviewId = reviewId, authorName = "($reviewId)번째 리뷰 팬", content = "이런 정보 아리가또요또요또요대전또요! ")
                    )
                } else {
                    listOf(
                        ReviewComment(reviewId = reviewId, authorName = "홀수리뷰 팬", content = "와우와우, ${reviewId}번째 동행후기도 알차네요!"),
                        ReviewComment(reviewId = reviewId, authorName = "($reviewId)번째 리뷰 팬", content = "와우 프로필 존잘 ㄷㄷ 차은우님도 동행을 가시네.")
                    )
                }
                commentsList.addAll(dummyComments)
            }
        }
    }








    // 댓글 추가 함수 (SnapshotStateList 직접 사용)
    fun addCommentToReview(reviewId: Int, author: String, content: String) {
        val newComment = ReviewComment(
            reviewId = reviewId,
            authorName = author,
            content = content
        ) // timestamp는 ReviewComment 데이터 클래스에서 기본값으로 현재 시간 설정
        // getCommentsForReview를 통해 해당 reviewId의 SnapshotStateList를 가져오고,
        // 여기에 직접 댓글을 추가합니다. Compose UI는 이 변경을 감지합니다.
        getCommentsForReview(reviewId).add(0, newComment) // 새 댓글을 맨 위에 추가
    }




}