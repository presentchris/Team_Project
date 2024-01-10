package com.example.vinonovi2.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit

data class DataItem(val image: String, val answer: String)

class ApiManager {
    suspend fun uploadImage(question: String): List<DataItem> =
        withContext(Dispatchers.IO) {
            // navDeepLink
            val url = "http://192.168.1.44:5000/predict"
            val client = OkHttpClient().newBuilder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "question",
                    question
                )
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val dataItemList: MutableList<DataItem> = emptyList<DataItem>().toMutableList()

            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // Image uploaded successfully
                    val responseBody = response.body?.string()

                    // Extract values from JSON
                    val jsonArray = JSONArray(responseBody)

                    for(i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val image: String = jsonObject.getString("image")
                        val answer: String = jsonObject.getString("answer")
                        Log.d("image", image)
                        Log.d("answer", answer)
                        val dataItem = DataItem(image, answer)

                        dataItemList += dataItem
                    }
                } else {
                    Log.e("망함", "망함")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return@withContext dataItemList
        }
}