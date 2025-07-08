// file: app/src/main/java/com/example/daejeonpass/model/CommentViewModel.kt
package com.example.daejeonpass.model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf // SnapshotStateList 사용을 위해 추가
import androidx.compose.runtime.mutableStateMapOf // mutableStateMapOf 사용을 위해 추가
import androidx.compose.runtime.snapshots.SnapshotStateList // 타입 명시를 위해 추가
// import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.viewModelScope
import com.example.DaejeonPass.R
import com.example.daejeonpass.data.ReviewComment // 데이터 클래스 임포트
import com.example.daejeonpass.data.ReviewThumbnailInfo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.daejeonpass.data.ReviewDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.AndroidViewModel
import com.example.daejeonpass.utils.drawablePngToUri


class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private fun getResourceIdByName(resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }


    // 탭 3에서 작성된 리뷰들의 목록 (탭 2에서 관찰)
    private val _reviews = mutableStateListOf<ReviewThumbnailInfo>()

    // 탭 3(프로필)에서 리뷰를 추가할 때 호출되는 함수
    fun addReview(newReview: ReviewThumbnailInfo) {
        // 이미 같은 reviewId가 있다면 업데이트, 없다면 추가 (간단한 예시)
        val existingIndex = _reviews.indexOfFirst { it.reviewId == newReview.reviewId }
        if (existingIndex != -1) {
            _reviews[existingIndex] = newReview
        } else {
            _reviews.add(newReview)
        }
    }

    // 리뷰 작성/수정 시 호출 (ReviewWriteScreen에서 호출)
    fun addOrUpdateReview(details: ReviewDetails) {
        // 상세 리뷰 목록에 추가 또는 업데이트
        val existingDetailsIndex = _reviewDetailsList.indexOfFirst { it.reviewId == details.reviewId }
        if (existingDetailsIndex != -1) {
            _reviewDetailsList[existingDetailsIndex] = details
            Log.d("ReviewViewModel", "Updated review details for ID: ${details.reviewId}")
        } else {
            _reviewDetailsList.add(0, details) // 최신순으로 맨 앞에 추가
            Log.d("ReviewViewModel", "Added new review details for ID: ${details.reviewId}")
        }
        // 날짜 순으로 정렬 (최신이 위로)
        _reviewDetailsList.sortByDescending { parseDate(it.date) }

        // 썸네일 목록에도 반영 (imageRes는 String 타입의 URI)
        // details.reviewImageRes는 ReviewDetails에 있는 String 타입의 이미지 URI
        val thumbnail = ReviewThumbnailInfo(reviewId = details.reviewId, imageRes = details.reviewImageRes)
        val existingThumbnailIndex = _reviewThumbnails.indexOfFirst { it.reviewId == thumbnail.reviewId }
        if (existingThumbnailIndex != -1) {
            _reviewThumbnails[existingThumbnailIndex] = thumbnail
            Log.d("ReviewViewModel", "Updated review thumbnail for ID: ${thumbnail.reviewId}")
        } else {
            _reviewThumbnails.add(0, thumbnail) // 최신순으로 맨 앞에 추가
            Log.d("ReviewViewModel", "Added new review thumbnail for ID: ${thumbnail.reviewId}")
        }
        // 썸네일도 상세 정보의 날짜 기준으로 정렬
        _reviewThumbnails.sortByDescending { thumb ->
            _reviewDetailsList.find { it.reviewId == thumb.reviewId }?.let { parseDate(it.date) }
        }

        Log.d("ReviewViewModel", "After addOrUpdate. Thumbnails: ${_reviewThumbnails.size}, Details: ${_reviewDetailsList.size}")
    }

    // 날짜 문자열 파싱 함수 (정렬용)
    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                ?: Date(0)
        } catch (e: Exception) {
            Log.e("ReviewViewModel", "Date parsing error for: $dateString", e)
            Date(0) // 파싱 실패 시 아주 오래된 날짜로 처리하여 정렬에 영향 최소화
        }
    }

    // GalleryScreen에 제공될 썸네일 정보 목록
    private val _reviewThumbnails = mutableStateListOf<ReviewThumbnailInfo>()
    val reviewThumbnails: SnapshotStateList<ReviewThumbnailInfo> get() = _reviewThumbnails

    // 실제 상세 리뷰 데이터 목록
    private val _reviewDetailsList = mutableStateListOf<ReviewDetails>()

    // 리뷰 상세 정보를 위한 StateFlow (ReviewDetailScreen에서 사용)
    private val _reviewDetails = MutableStateFlow<ReviewDetails?>(null)
    val reviewDetails: StateFlow<ReviewDetails?> = _reviewDetails.asStateFlow()

    // --- 댓글 데이터 관리 방식 변경 ---
    // 이전: val commentsState by viewModel.getCommentsFlow(reviewId).collectAsState()
    // 변경: SnapshotStateList를 직접 사용
    // 이유: 전에꺼는 비동기 스트림 구독 방식이라 Flow 갱신 코드 필요한데 밑에꺼는 Compose 내장 상태 관리방식이라 그냥 리스트 접근 됨
    // Key: reviewId, Value: 해당 리뷰의 댓글 목록 (SnapshotStateList)
    private val _commentsMap = mutableStateMapOf<Int, SnapshotStateList<ReviewComment>>()



    // 외부에 노출 시에는 불변 Map으로 노출할 수 있으나, Composable에서 직접 사용 시 SnapshotStateMap 자체를 사용해도 됨
    // 여기서는 Composable에서 reviewId로 직접 접근하여 SnapshotStateList를 사용하도록 유도
    fun getCommentsForReview(reviewId: Int): SnapshotStateList<ReviewComment> {
        return _commentsMap.getOrPut(reviewId) {
            mutableStateListOf() // 해당 reviewId에 대한 리스트가 없으면 새로 생성
        }
    }
    // ReviewDetailScreen에서 사용할 함수
    fun getReviewDetailsById(reviewId: Int): ReviewDetails? {
        return _reviewDetailsList.find { it.reviewId == reviewId }
    }


    init {
        // ViewModel 생성 시 더미 데이터 추가 (실제로는 비어있거나 DB에서 로드)
        addInitialDummyReviews() // 더미 데이터 추가 함수 호출

        // 댓글 더미 데이터 (기존 유지)
        val dummyReviewIdForInitialComments = 21 // 위에서 추가한 reviewId로 변경
        val initialCommentsList = getCommentsForReview(dummyReviewIdForInitialComments)
        if (initialCommentsList.isEmpty()) {
            initialCommentsList.addAll(
                listOf(
                    ReviewComment(
                        reviewId = dummyReviewIdForInitialComments,
                        authorName = "김투어", content = "성심당 최고!",
                        timestamp = System.currentTimeMillis(),
                        profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png")
                    )
                )
            )
        }
    }

    private fun addInitialDummyReviews() {
        val dummyReviewsData = mutableListOf<ReviewDetails>()
        for (i in 1..20) {
            val drawableName = "sample$i"
            val imageResId = getResourceIdByName(drawableName)

            if (imageResId != 0) {
                dummyReviewsData.add(
                    ReviewDetails(
                        reviewId = i,
                        title = "대전 여행 후기 $i",
                        content = "정말 즐거운 대전 여행이었어요! 특히 장소 ${i}이(가) 인상적이었습니다. (내용 $i)",
                        authorName = "여행객 $i",
                        profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png"),
                        reviewImageRes = "android.resource://${context.packageName}/$imageResId",
                        date = "2024-0${(i % 12) + 1}-${(i % 28) + 1}",
                        rating = (i % 5 + 1).toFloat()
                    )
                )
            } else {
                Log.w("ReviewViewModel", "Drawable resource not found: $drawableName")
            }
        }

        dummyReviewsData.forEach { reviewDetail ->
            addOrUpdateReview(reviewDetail)
        }

        Log.d("ReviewViewModel", "Added ${dummyReviewsData.size} initial dummy reviews.")
    }



    // 리뷰 상세 정보와 댓글을 로드하는 함수
    // imageResFromNav 타입을 Int에서 String으로 변경
    fun loadReviewData(reviewId: Int, imageResFromNav: String) {
        viewModelScope.launch {
            Log.d("ReviewViewModel", "Loading review data for ID: $reviewId, Image: $imageResFromNav")
            // 상세 정보 로드 (기존 _reviewDetails.value?.reviewId == reviewId 비교는
            // imageResFromNav도 고려해야 할 수 있으므로, _reviewDetailsList에서 직접 찾는 것으로 변경)

            val fetchedDetails = _reviewDetailsList.find { it.reviewId == reviewId && it.reviewImageRes == imageResFromNav }

            if (fetchedDetails != null) {
                _reviewDetails.value = fetchedDetails
                Log.d("ReviewViewModel", "Found review in _reviewDetailsList: ${fetchedDetails.title}")
            } else {
                // _reviewDetailsList에 없는 경우 (예: 초기 더미 데이터에 없거나, ID/ImageRes 불일치)
                // 여기에 더미 ReviewDetails를 생성하는 것은 실제 상황에서는 부적절할 수 있음.
                // GalleryScreen에서 클릭해서 왔다면 _reviewDetailsList에 반드시 있어야 함.
                // 만약 없다면 로직 오류 또는 데이터 불일치 가능성.
                Log.w("ReviewViewModel", "Review NOT FOUND in _reviewDetailsList for ID: $reviewId, Image: $imageResFromNav. Creating dummy details.")
                // 임시 더미 데이터 (오류 상황 또는 테스트용)
                _reviewDetails.value = ReviewDetails(
                    reviewId = reviewId,
                    title = "리뷰 정보 없음 ($reviewId)",
                    content = "해당 리뷰 정보를 찾을 수 없습니다. 이미지: $imageResFromNav",
                    authorName = "시스템",
                    profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png"), // 기본 프로필
                    reviewImageRes = imageResFromNav, // 전달받은 이미지 URI 사용
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    rating = 0f
                )
            }

            // 댓글 데이터 로드 (기존 로직 유지)
            val commentsList = getCommentsForReview(reviewId)
            if (commentsList.isEmpty() && reviewId != 21 /* init에서 이미 처리한 ID 제외 */) {
                // ... (기존 댓글 더미 데이터 생성 로직) ...
                Log.d("ReviewViewModel", "Loading dummy comments for review ID: $reviewId")
                val dummyComments = if (reviewId % 2 == 0) {
                    listOf(
                        ReviewComment( reviewId = reviewId, authorName = "짝수리뷰 팬",
                            profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png"),
                            content = "${reviewId}번째 동행 후기도 아주 좋아요!", timestamp = System.currentTimeMillis(),),
                        ReviewComment( reviewId = reviewId, authorName = "($reviewId)번째 리뷰 팬",
                            profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png"),
                            content = "이런 정보 아리가또요또요또요대전또요!", timestamp = System.currentTimeMillis(),)
                    )
                } else {
                    listOf(
                        ReviewComment( reviewId = reviewId, authorName = "홀수리뷰 팬",
                            profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png"),
                            content = "와우와우, ${reviewId}번째 동행후기도 알차네요!", timestamp = System.currentTimeMillis(),),
                        ReviewComment( reviewId = reviewId, authorName = "($reviewId)번째 리뷰 팬",
                            profileImageUri= context.drawablePngToUri(R.drawable.basic_profile, "basic_profile.png"),
                            content = "와우 프로필 존잘 ㄷㄷ 변우석님도 동행을 가시네.", timestamp = System.currentTimeMillis(),)
                    )
                }
                commentsList.addAll(dummyComments)
            }
        }
    }




    // 댓글 추가 함수 (SnapshotStateList 직접 사용)
    fun addCommentToReview(reviewId: Int, userProfile: UserProfile, content: String) {
        val newComment = ReviewComment(
            reviewId = reviewId,
            authorName = userProfile.name,
            content = content,
            timestamp = System.currentTimeMillis(),
            profileImageUri = userProfile.profileImage
        ) // timestamp는 ReviewComment 데이터 클래스에서 기본값으로 현재 시간 설정
        // getCommentsForReview를 통해 해당 reviewId의 SnapshotStateList를 가져오고,
        // 여기에 직접 댓글을 추가합니다. Compose UI는 이 변경을 감지합니다.
        getCommentsForReview(reviewId).add(0, newComment) // 새 댓글을 맨 위에 추가
    }
}