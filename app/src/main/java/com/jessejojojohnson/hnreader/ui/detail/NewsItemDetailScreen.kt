package com.jessejojojohnson.hnreader.ui.detail

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.java.KoinJavaComponent.get

@Composable
fun NewsItemDetailScreen(
    viewModel: NewsItemDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    itemId: String
) {
    val newsItem = remember {
        mutableStateOf(HNStoryEntity.default)
    }
    LaunchedEffect(Unit) {
        viewModel.getItemFlow(itemId).collect {
            newsItem.value = it
        }
    }
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        item {
            Text(
                text = newsItem.value.title,
                style = MaterialTheme.typography.h5,
                textDecoration = TextDecoration.Underline,
                fontFamily = FontFamily.Serif
            )
        }
        item {
            DetailsWebView(url = newsItem.value.url)
        }
    }
}

@Composable
fun DetailsWebView(url: String) {
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}

class NewsItemDetailViewModel : ViewModel() {
    private val db: AppDatabase = get(AppDatabase::class.java)

    fun getItem(itemId: String) = db.hnEntityDao().get(itemId)
    fun getItemFlow(itemId: String) = db.hnEntityDao().getFlow(itemId).distinctUntilChanged()
}