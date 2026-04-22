package com.team2.studentfitness.ui.components.youtube

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private fun buildYouTubeEmbedHtml(videoId: String): String {
    val embedUrl = "https://www.youtube-nocookie.com/embed/$videoId?rel=0&playsinline=1&modestbranding=1"
    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
          <style>
            html, body {
              margin: 0;
              padding: 0;
              width: 100%;
              height: 100%;
              background: #000;
              overflow: hidden;
            }
            .frame-wrap {
              position: fixed;
              inset: 0;
              display: flex;
              align-items: center;
              justify-content: center;
            }
            iframe {
              width: 100%;
              height: 100%;
              border: 0;
            }
          </style>
        </head>
        <body>
          <div class="frame-wrap">
            <iframe
              src="$embedUrl"
              allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture; web-share"
              allowfullscreen>
            </iframe>
          </div>
        </body>
        </html>
    """.trimIndent()
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbedPlayer(
    videoId: String,
    modifier: Modifier = Modifier,
    onLoadFailed: () -> Unit = {}
) {
    val latestOnLoadFailed = rememberUpdatedState(onLoadFailed)
    val html = remember(videoId) { buildYouTubeEmbedHtml(videoId) }
    val webViewHolder = remember(videoId) { mutableStateOf<WebView?>(null) }

    DisposableEffect(videoId) {
        onDispose {
            webViewHolder.value?.destroy()
            webViewHolder.value = null
        }
    }

    key(videoId) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewHolder.value = this
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            if (request?.isForMainFrame == true) {
                                latestOnLoadFailed.value()
                            }
                        }
                    }
                    loadDataWithBaseURL(
                        "https://www.youtube-nocookie.com",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            }
        )
    }
}

@Composable
fun EmbeddedYouTubeVideo(
    videoId: String,
    onLoadFailed: () -> Unit = {},
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
) {
    YouTubeEmbedPlayer(
        videoId = videoId,
        modifier = modifier,
        onLoadFailed = onLoadFailed
    )
}

