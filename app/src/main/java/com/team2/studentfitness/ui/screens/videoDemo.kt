package com.team2.studentfitness.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoDemoScreen(
    videoId: String = "M7lc1UVf-VE"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerViewHolder = remember { mutableStateOf<YouTubePlayerView?>(null) }
    val playerHolder = remember { mutableStateOf<YouTubePlayer?>(null) }
    val tracker = remember { YouTubePlayerTracker() }
    val embedFailed = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            playerHolder.value = null
            playerViewHolder.value?.let { view ->
                lifecycleOwner.lifecycle.removeObserver(view)
                view.release()
            }
            playerViewHolder.value = null
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("YouTube IFrame API Demo") }) }
    ) { innerPadding ->
        if (embedFailed.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("The embedded player did not load.")
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=$videoId".toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open watch page")
                }
            }
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .aspectRatio(16f / 9f),
                factory = { ctx ->
                    YouTubePlayerView(ctx).apply {
                        playerViewHolder.value = this
                        lifecycleOwner.lifecycle.addObserver(this)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        val playerOptions = IFramePlayerOptions.Builder()
                            .controls(1)
                            .fullscreen(1)
                            .rel(0)
                            .ivLoadPolicy(3)
                            .build()

                        initialize(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                playerHolder.value = youTubePlayer
                                youTubePlayer.addListener(tracker)
                                youTubePlayer.cueVideo(videoId, 0f)
                            }

                            override fun onError(
                                youTubePlayer: YouTubePlayer,
                                error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError
                            ) {
                                embedFailed.value = true
                            }
                        }, true, playerOptions)
                    }
                },
                update = {
                    playerHolder.value?.let { player ->
                        if (tracker.videoId != videoId) {
                            embedFailed.value = false
                            player.cueVideo(videoId, 0f)
                        }
                    }
                }
            )
        }
    }
}
