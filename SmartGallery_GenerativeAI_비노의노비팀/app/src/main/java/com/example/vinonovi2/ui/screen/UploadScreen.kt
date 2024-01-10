package com.example.vinonovi2.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vinonovi2.data.Image
import com.example.vinonovi2.data.ImageDatabase
import com.example.vinonovi2.network.FirebaseStorageManager
import com.example.vinonovi2.ui.component.LoadingCircle
import com.example.vinonovi2.ui.navigation.Screen
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UploadScreen(navController: NavController) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val db = remember {
        ImageDatabase.getDatabase(context)
    }
    var selectUriList by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val launcher = // 갤러리 이미지 런쳐
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                if (uris.isNotEmpty()) {
                    selectUriList = uris

                }
            }
        )
    var loading by remember {
        mutableStateOf(false)
    }
    if (loading) {
        LoadingCircle("사진을 업로드 하는 중입니다...")
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFF0D3CD))
            ) {
                Text(
                    text = "UPLOAD",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Color.White)
            ) {
                IconButton(
                    onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .size(100.dp)
                ) {
                    Icon(
                        Icons.Filled.PhotoAlbum,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(300.dp)
                    )
                }
                Text(
                    text = "앨범 열기",
                    fontSize = 15.sp,
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp)
            ) {

                items(selectUriList) { selectUri ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clickable {
                                // 항목을 클릭하여 선택을 토글합니다.
                            },
                    ) {
                        GlideImage(
                            modifier = Modifier
                                .fillMaxSize(),
                            //                                .aspectRatio(1f)
                            //                                .clip(shape = RoundedCornerShape(16.dp)),
                            //                                .background(MaterialTheme.colorScheme.background),
                            imageModel = { selectUri },
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            ),
                            loading = {
                                LoadingCircle(text = "")
                            },
                            // shows an error text if fail to load an image.
                            failure = {
                                Text(text = "image request failed.")
                            }
                        )

                    }

                }
            }


        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ExtendedFloatingActionButton(
                icon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) },
                text = { Text("Add") },
                onClick = {
                    loading = true
                    scope.launch(Dispatchers.IO) {
                        try {
                            val firebaseStorageManager = FirebaseStorageManager()

                            for (selectUri in selectUriList) {
                                val imageUrl =
                                    firebaseStorageManager.uploadImage(contentResolver, selectUri)
                                imageUrl?.let { uri ->
                                    val newImage = Image(imageUrl = uri.toString())
                                    db.imageDao().insertAll(newImage)
                                }
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "이미지 업로드 성공", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Gallery.route) {
                                    popUpTo(Screen.Gallery.route) {
                                        inclusive = true
                                    }
                                }
                                delay(500)
                                loading = false
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                                loading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = Color.Transparent)
                    .clip(CircleShape) // 원 모양으로 클리핑
                    .size(56.dp) // 지정된 크기
            )
        }
    }
}


