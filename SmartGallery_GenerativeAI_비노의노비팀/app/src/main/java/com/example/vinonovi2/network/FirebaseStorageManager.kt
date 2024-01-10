package com.example.vinonovi2.network

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseStorageManager {
    private val storageRef = Firebase.storage.reference
    private val storageReference: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    suspend fun uploadImage(contentResolver: ContentResolver, imageUri: Uri): Uri? {
        // 이미지 파일의 MIME 타입을 얻기
        val mimeType = contentResolver.getType(imageUri)

        // 이미지 파일의 확장자 추출
        val fileExtension =
            mimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }

        // Firebase Storage에 저장할 이미지 파일 이름 생성
        val imageName = "${UUID.randomUUID()}.$fileExtension"

        // Firebase Storage에 이미지 업로드
        val imageRef = storageReference.child(imageName)
        val uploadTask = imageRef.putFile(imageUri)

        return try {
            // 업로드 완료를 기다림
            val result = uploadTask.await()

            // 업로드된 이미지의 다운로드 URL 반환
            result.storage.downloadUrl.await()
        } catch (e: Exception) {
            // 업로드 실패 시 예외 처리
            null
        }
    }

    suspend fun downloadImage(imageFileName: String): Uri? {
        // Storage Reference 생성
        val imageRef = storageRef.child(imageFileName)

        return try {
            // 이미지 다운로드 URL 반환
            imageRef.downloadUrl.await()
        } catch (e: Exception) {
            // 다운로드 실패 시 예외 처리
            null
        }
    }
}