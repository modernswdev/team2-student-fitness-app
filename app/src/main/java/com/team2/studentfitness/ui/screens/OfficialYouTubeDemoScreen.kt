package com.team2.studentfitness.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.team2.studentfitness.ui.components.youtube.YouTubeEmbedPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialYouTubeDemoScreen(
    videoId: String = "M7lc1UVf-VE"
) {
    val context = LocalContext.current
    var loadFailed by remember { mutableStateOf(false) }

    fun openInYouTube() {
        val appIntent = Intent(
            Intent.ACTION_VIEW,
            "vnd.youtube:$videoId".toUri()
        ).apply {
            setPackage("com.google.android.youtube")
        }

        val webIntent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=$videoId".toUri())

        try {
            context.startActivity(appIntent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(webIntent)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Official YouTube Demo") }) }
    ) { innerPadding ->
        if (loadFailed) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "The in-app YouTube page failed to load.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { openInYouTube() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open in YouTube app")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=$videoId".toUri()))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open watch page in browser")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Embedded YouTube player.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                YouTubeEmbedPlayer(
                    videoId = videoId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    onLoadFailed = { loadFailed = true }
                )
            }
        }
    }
}
