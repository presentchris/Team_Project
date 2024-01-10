package com.example.getiskin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@Composable
fun HomeReturnButton(navController: NavController) {

    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                navController.navigate("home")
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "홈 화면으로",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun ClinicSearchButton() {
    val searchText = "피부관리"
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val context = LocalContext.current
    var isLocationPermissionGranted by remember { mutableStateOf(false) }
    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isLocationPermissionGranted = isGranted
        }
    var isClicked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                isClicked = true
            }
    ) {
        if (isClicked) {
            // 클릭되었을 때의 UI
            // 예를 들어, 광고 클릭 후에 할 작업을 여기에 추가

            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 이미 권한이 있는 경우
                isLocationPermissionGranted = true
                if (isLocationPermissionGranted) {
                    // 위치 권한이 허용된 경우 위치 정보 가져오기 시도
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            // 위치 정보 가져오기 성공
                            val currentLatLng =
                                LatLng(location.latitude, location.longitude)

                            // 검색어와 현재 위치 기반으로 Google Maps 열기
                            openGoogleMaps(context, currentLatLng, searchText)
                        } else {
                            // 위치 정보 없음
                        }
                    }.addOnFailureListener { exception ->
                        // 위치 정보 가져오기 실패
                    }
                } else {
                    // 위치 권한이 거부된 경우 처리
                    // 여기에 권한이 거부되었을 때의 동작을 추가할 수 있습니다.
                }
            } else {
                // 권한이 없는 경우
                requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.clinics),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "피부관리샵 찾기",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun ClinicScreen(navController: NavController) {

    val complexAd = "https://www.coupang.com/np/search?component=&q=%EB%B3%B5%ED%95%A9%EC%84%B1+%ED%99%94%EC%9E%A5%ED%92%88&channel=user"
    val hwahaeAd = "https://www.hwahae.co.kr/"

    val searchText = "피부관리"
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val context = LocalContext.current
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    // 위치 권한 요청을 위한 런처
    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isLocationPermissionGranted = isGranted
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor("#ffffff")))
    ) {
        // 상단 이미지
        Image(
            painter = painterResource(id = R.drawable.logo), // 이미지 리소스 ID로 변경
            contentDescription = null, // contentDescription은 필요에 따라 추가
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 이미지 높이 조절
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            // Text
            Text(
                text = "C L I N I C",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(android.graphics.Color.parseColor("#F7F1E5")))
                    .padding(20.dp)
                    .height(30.dp), // 여백 추가
                textAlign = TextAlign.Center,
                fontSize = 24.sp, // 원하는 크기로 조절
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#e39368")) // 원하는 색상으로 조절
            )

            // 중앙 컨텐츠
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AdPlaces("복합성 화장품 광고", complexAd)
                Spacer(modifier = Modifier.height(80.dp))
                // Button
                ClinicSearchButton()
                Spacer(modifier = Modifier.height(20.dp))
                HomeReturnButton(navController)
                Spacer(modifier = Modifier.height(70.dp))

                AdPlaces("화해 광고", hwahaeAd)
            }

        }

    }
}


// Google Maps 열기 함수
private fun openGoogleMaps(context: Context, destinationLatLng: LatLng, searchText: String) {
    val mapIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:${destinationLatLng.latitude},${destinationLatLng.longitude}?q=$searchText")
    )

    // Google Maps 앱이 설치되어 있는 경우 Google Maps 앱에서 지도를 엽니다.
    // 설치되어 있지 않은 경우 웹 브라우저에서 지도를 엽니다.
    mapIntent.setPackage("com.google.android.apps.maps")
    context.startActivity(mapIntent)
}

@Composable
fun PreviewClinicScreen() {
    // 여기서는 NavController를 mock으로 사용하거나, 필요에 따라 빈 NavController를 생성할 수 있습니다.
    val navController = rememberNavController() // 빈 NavController 생성

    // 미리보기에서 사용할 가상의 데이터 등을 여기에 추가할 수 있습니다.

    ClinicScreen(navController = navController)
}

@Preview
@Composable
fun ClinicScreenPreview() {
    PreviewClinicScreen()
}