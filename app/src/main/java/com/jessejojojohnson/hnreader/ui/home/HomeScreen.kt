package com.jessejojojohnson.hnreader.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import com.jessejojojohnson.hnreader.network.GetStoriesWorker
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.java.KoinJavaComponent.get

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
    NewsItemList(newsItems = items.value, onItemClick = onItemClick)
    BtnRefresh()
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
        .padding(8.dp)
        .clickable {
            onItemClick.invoke(newsItem.id)
        }) {
        Text(
            text = newsItem.title,
            style = MaterialTheme.typography.body1
        )
        Text(
            text = newsItem.by,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun BtnRefresh(modifier: Modifier = Modifier.wrapContentSize()) {
    val workManager: WorkManager = get(WorkManager::class.java)
    Button(
        modifier = modifier,
        onClick = {
            workManager.enqueue(
                OneTimeWorkRequestBuilder<GetStoriesWorker>().build()
            )
        }) {
        Text(text = "Refresh")
    }
}