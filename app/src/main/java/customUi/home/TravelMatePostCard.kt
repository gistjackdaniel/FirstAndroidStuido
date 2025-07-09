package customUi.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.daejeonpass.model.TravelMatePost
import kotlin.math.round

/**
 * ✅ 모집글 카드 UI를 표시하는 Composable 함수
 * - 각 모집글 정보를 카드 형태로 깔끔하게 보여줌
 *
 * @param post 표시할 모집글 데이터 (TravelMatePost)
 */
@Composable
fun TravelMatePostCard(
      post: TravelMatePost,
      onClick: () -> Unit
) {
      fun formatPrice(price: Int): String {
            return "%,d".format(price)
      }

      val priceInt = post.price.toInt()

      val priceText =
            when{
                  priceInt <= 50000 -> "50,000 ￦ ▼"
                  priceInt >= 100000 -> "100,000 ￦ ▲"
                  else -> {
                        val rounded = round(priceInt / 1000.0) * 1000
                        "${formatPrice(rounded.toInt())} ￦"
                  }
            }

      Card(
            modifier = Modifier
                  .fillMaxWidth()
                  .padding(3.dp) //카드 외부 여백
                  .clickable { onClick() }, //카드 클릭 가능
            elevation = CardDefaults.cardElevation(4.dp)
      ) {
            Box(
                  modifier= Modifier
                        .fillMaxSize()
                        .padding(4.dp)
            ) {
                  Column(
                        modifier = Modifier.padding(16.dp)
                  ) {
                        Row(
                              verticalAlignment = Alignment.CenterVertically
                        ) {
                              post.profileImage?.let { uri->
                                    Image(
                                          painter = rememberAsyncImagePainter(uri),
                                          contentDescription = "작성자 프로필",
                                          modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                    )
                              }

                              Spacer(modifier = Modifier.width(8.dp))

                              Column {
                                    Text( // 이름
                                          text = post.authorName,
                                          style = MaterialTheme.typography.titleMedium,
                                          fontWeight = FontWeight.Bold
                                    )
                                    Text( // 게시물 제목
                                          text = post.title,
                                          style = MaterialTheme.typography.bodySmall
                                    )
                              }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                              Text(
                                    text = post.date,
                                    style = MaterialTheme.typography.bodySmall
                              )

                              Spacer(modifier = Modifier.width(16.dp))

                              Text( // 장소
                                    text = post.location,
                                    style = MaterialTheme.typography.bodySmall
                              )
                        }
                  }

                  Row(
                        modifier = Modifier
                              .align(Alignment.TopEnd)
                              .padding(top = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                  ) {
                        if(post.isBookmarked){
                              Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "북마크됨",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Yellow
                              )
                              Spacer(modifier = Modifier.width(4.dp))
                        }

                        Icon(
                              imageVector = Icons.Default.Person,
                              contentDescription = "참여 인원",
                              modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        if((post.totalpeople-post.currentpeople)<=1){
                              Text(
                                    text = "${post.currentpeople} / ${post.totalpeople}명",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Red
                              )
                        }
                        else{
                              Text(
                                    text = "${post.currentpeople} / ${post.totalpeople}명",
                                    style = MaterialTheme.typography.bodySmall
                              )
                        }

                  }

                  OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                              .align(Alignment.BottomEnd)
                              .padding(4.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 1.dp)
                  ){
                        Text(
                              text = priceText,
                              style = MaterialTheme.typography.labelSmall
                        )

                  }
            }
      }
}