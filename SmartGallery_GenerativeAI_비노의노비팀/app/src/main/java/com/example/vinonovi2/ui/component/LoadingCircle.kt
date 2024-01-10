package com.example.vinonovi2.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoadingCircle(text: String) {
    Box(modifier = Modifier.fillMaxSize()) {
            Text(text = text,
                modifier = Modifier.fillMaxSize()
                    .padding(top = 100.dp),
                textAlign = TextAlign.Center)
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
                .size(150.dp),
            color = Color.DarkGray,
        )
    }
}