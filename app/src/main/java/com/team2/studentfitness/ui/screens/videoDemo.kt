package com.team2.studentfitness.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import android.view.ViewGroup

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoDemoScreen(
    videoId: String = "sHwvUFjaNdU"
) {
    val context = LocalContext.current
    val webViewHolder = remember { mutableStateOf<WebView?>(null) }
    val embedFailed = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            webViewHolder.value?.apply {
                loadUrl("about:blank")
                stopLoading()
                destroy()
            }
            webViewHolder.value = null
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("YouTube Embed Test") }) }
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
                    WebView(ctx).apply {
                        webViewHolder.value = this
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(android.graphics.Color.BLACK)

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadsImagesAutomatically = true
                        settings.javaScriptCanOpenWindowsAutomatically = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                if (request?.isForMainFrame == true) {
                                    embedFailed.value = true
                                }
                            }
                        }
                        webChromeClient = WebChromeClient()

                        loadDataWithBaseURL(
                            "https://www.youtube.com",
                            buildYoutubeEmbedHtml(videoId),
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                },
                update = { view ->
                    if (view.url == null) {
                        view.loadDataWithBaseURL(
                            "https://www.youtube.com",
                            buildYoutubeEmbedHtml(videoId),
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                }
            )
        }
    }
}

private fun buildYoutubeEmbedHtml(videoId: String): String = """
    <html>
      <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
        <style>
          html, body {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            min-height: 100vh;
            background: #000;
            overflow: hidden;
          }
          #player {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
          }
          iframe {
            width: 100%;
            height: 100%;
            border: 0;
            display: block;
          }
        </style>
      </head>
      <body>
        <div id="player">
          <iframe
            width="100%"
            height="100%"
            src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0&modestbranding=1"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            allowfullscreen>
          </iframe>
        </div>
      </body>
    </html>
""".trimIndent()
