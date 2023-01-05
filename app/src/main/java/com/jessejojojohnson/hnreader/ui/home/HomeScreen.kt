package com.jessejojojohnson.hnreader.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import com.jessejojojohnson.hnreader.network.GetStoriesWorker
import com.jessejojojohnson.hnreader.ui.theme.Orange
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.java.KoinJavaComponent.get


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onItemClick: (String) -> Unit
) {
    val items = rememberSaveable { mutableStateOf<List<HNStoryEntity>>(emptyList()) }
    LaunchedEffect(Unit) {
        viewModel.getItemsFlow().collect {
            items.value = it
        }
    }
    Scaffold(
        floatingActionButton = { BtnRefresh() },
        topBar = { Header(text = "HN Daily Digest") },
        content = {
            NewsItemList(newsItems = items.value, onItemClick = onItemClick)
        }
    )
}

class HomeScreenViewModel : ViewModel() {
    private val db: AppDatabase = get(AppDatabase::class.java)

    fun getItems(): List<HNStoryEntity> = db.hnEntityDao().getAll()
    fun getItemsFlow() = db.hnEntityDao().getAllFlow().distinctUntilChanged()
}

@Composable
fun NewsItemList(
    newsItems: List<HNStoryEntity>,
    onItemClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(newsItems) { item ->
            NewsItemRow(newsItem = item, onItemClick = onItemClick)
        }
    }
}

@Composable
fun NewsItemRow(
    newsItem: HNStoryEntity,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onItemClick.invoke(newsItem.id)
        }) {
        Text(
            text = newsItem.title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
        )
        Text(
            text = newsItem.url,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
fun BtnRefresh() {
    FloatingActionButton(
        modifier = Modifier.padding(5.dp),
        onClick = {
            val workManager: WorkManager = get(WorkManager::class.java)
            workManager.enqueue(
                OneTimeWorkRequestBuilder<GetStoriesWorker>().build()
            )
        },
        shape = CircleShape,
        backgroundColor = Orange
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Refresh",
            tint = Color.White
        )
    }
}

@Composable
fun Header(text: String) {
    Column {
        Text(
            text,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Divider(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            color = Color.LightGray
        )
    }
}