package customUi.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.daejeonpass.data.PostRepository
import com.example.daejeonpass.model.TravelMatePost
import com.example.daejeonpass.model.UserProfile
import com.example.DaejeonPass.R
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.LocalDate

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPost(
    navController: NavController,
    writer: UserProfile
){
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember{ mutableStateOf("") }
    var date by remember{ mutableStateOf("") }
    var location by remember{ mutableStateOf("") }
    var price by remember{ mutableStateOf("") }
    var totalpeople by remember{ mutableIntStateOf(0) }
    var gita by remember{ mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    if(showDatePicker){
        val today = LocalDate.now()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                date = String.format("%04d-%02d-%02d",year,month+1,day)
                showDatePicker = false
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.setOnCancelListener {showDatePicker = false}
        datePickerDialog.show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("글쓰기") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "뒤로가기")
                    }
                })}
    ){  innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp) // 내부 여백
            ){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Newspaper, contentDescription = "날짜 선택")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Default.AccessTime, contentDescription = "날짜 선택")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text("날짜") },
                        readOnly = true,
                        modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "날짜 선택")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Default.Map, contentDescription = "장소 선택")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("장소") },
                        modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Default.PeopleAlt, contentDescription = "날짜 선택")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("인원 : ", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    var isExpanded by remember { mutableStateOf(false) }

                    Box{
                        OutlinedButton(onClick={ isExpanded = true }){
                            Text("$totalpeople 명")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "인원 선택")
                        }
                        DropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ){
                            for(i in 2..10){
                                DropdownMenuItem(
                                    text = { Text("$i 명") },
                                    onClick = {
                                        totalpeople = i
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachMoney, contentDescription = "날짜 선택")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { cost -> cost.isDigit() } },
                        label = { Text("금액") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CoPresent, contentDescription = "날짜 선택")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = gita,
                        onValueChange = { gita = it },
                        label = { Text("기타") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val priceToInt = price.toIntOrNull() ?: 0

                        if(priceToInt>1000000){
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "금액이 너무 큽니다. (1,000,000원 이하)",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
                            }
                        }
                        else if(totalpeople<=1){
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "인원을 골라주세요",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
                            }
                        }
                        else if(title=="" || date=="" || location=="" || price=="" || gita==""){
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "항목을 모두 입력해주세요",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
                            }
                        }
                        else {
                            val newPost = TravelMatePost(
                                id = 0, // 큰 의미 X, 어짜피 PostRepository에서 관리
                                profileImage = writer.profileImage,
                                authorName = writer.name,
                                gender = writer.gender,
                                age = writer.age,
                                title = title,
                                date = date,
                                location = location,
                                price = price.toInt(),
                                totalpeople = totalpeople,
                                currentpeople = 1,
                                gita = gita,
                                isBookmarked = false
                            )

                            PostRepository.createAndAddPost(newPost)

                            navController.navigate("home_screen") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            navController.popBackStack() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                ){
                    Text("게시물 등록")
                }
            }
    }
}