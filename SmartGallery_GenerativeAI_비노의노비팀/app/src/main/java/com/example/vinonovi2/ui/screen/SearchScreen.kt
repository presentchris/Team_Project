@file:Suppress("DEPRECATION")

package com.example.vinonovi2.ui.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.vinonovi2.network.ApiManager
import com.example.vinonovi2.network.DataItem
import com.example.vinonovi2.network.FirebaseStorageManager
import com.example.vinonovi2.ui.component.LoadingCircle
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val apiManager = ApiManager()
    val scope = rememberCoroutineScope()
    var question by remember {
        mutableStateOf("")
    }
    var dataItemList: List<DataItem> by remember {
        mutableStateOf(emptyList())
    }
    var firebaseStorageManager = FirebaseStorageManager()
    var storageUriList by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    var loading by remember {
        mutableStateOf(false)
    }
    val dialogOpened = remember { mutableStateOf(false) }
    var clickStorageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xFFF7D9D3))
        ) {
            Text(
                text = "SEARCH",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,

            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 16.dp)
                .background(Color.White)
        ) {
            OutlinedTextField(
                value = question,
                label = { Text(text = "검색어를 입력하세요") },
                onValueChange = { question = it },
                colors = TextFieldDefaults.textFieldColors(
                    focusedLabelColor = Color.Black, containerColor = Color.White
                ),
                modifier = Modifier
                    .background(Color.White)
                    .height(60.dp)
//                    .clip(RoundedCornerShape(20.dp))
                    .width(300.dp)
            )
            Spacer(Modifier.width(10.dp))

            IconButton(
                onClick = {
                    if (question != "") {
                        loading = true
                        storageUriList = emptyList()
                        scope.launch {
                            dataItemList = apiManager.uploadImage(question)
                            for (dataItem in dataItemList) {
                                if (dataItem.answer.equals("yes")) {
                                    storageUriList += firebaseStorageManager.downloadImage(dataItem.image)
                                }
                            }
                            loading = false
                        }
                    }
                },
            ) {
                Icon(
                    Icons.Filled.Search, contentDescription = "Localized description",
                    modifier = Modifier.size(50.dp)
                )
            }

        }
        if (loading) {
            LoadingCircle("AI 검색이 실행중입니다...")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                if (storageUriList.isNotEmpty()) {
                    items(storageUriList) { storageUri ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clickable {
                                    clickStorageUri = storageUri
                                    dialogOpened.value = true
                                },
                        ) {
                            GlideImage(
                                modifier = Modifier
                                    .fillMaxSize(),
//                                .aspectRatio(1f)
//                                .clip(shape = RoundedCornerShape(16.dp)),
//                                .background(MaterialTheme.colorScheme.background),
                                imageModel = { storageUri },
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
            if (dialogOpened.value) {
                ImagePopup(
                    imageUri = clickStorageUri,
                    onCloseClicked = {
                        dialogOpened.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun ImagePopup(imageUri: Uri?, onCloseClicked: () -> Unit) {
    Dialog(
        onDismissRequest = { onCloseClicked() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 이미지 크게 보여주기
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { imageUri },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
            )
        }
    }
}

