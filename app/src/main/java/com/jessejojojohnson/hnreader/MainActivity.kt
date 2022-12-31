package com.jessejojojohnson.hnreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import com.jessejojojohnson.hnreader.network.GetStoriesWorker
import com.jessejojojohnson.hnreader.ui.theme.HNReaderTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.java.KoinJavaComponent.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HNReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel()
) {
    val items = rememberSaveable { mutableStateOf<List<HNStoryEntity>>(emptyList()) }
    LaunchedEffect(Unit) {
        viewModel.getItemsFlow().collect {
            items.value = it
        }
    }
    NewsItemList(newsItems = items.value)
    BtnRefresh()
}

@Composable
fun NewsItemList(
    newsItems: List<HNStoryEntity>
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(newsItems) { item ->
            NewsItemRow(newsItem = item)
        }
    }
}

@Composable
fun NewsItemRow(
    newsItem: HNStoryEntity
) {
    Column(modifier = Modifier.padding(8.dp)) {
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
    Button(
        modifier = modifier,
        onClick = {
            WorkManager.getInstance().enqueue(
                    OneTimeWorkRequestBuilder<GetStoriesWorker>().build()
                )
        }) {
        Text(text = "Refresh")
    }
}

class HomeScreenViewModel : ViewModel() {

    private val db: AppDatabase = get(AppDatabase::class.java)

    fun getItems(): List<HNStoryEntity> = db.hnEntityDao().getAll()
    fun getItemsFlow() = db.hnEntityDao().getAllFlow().distinctUntilChanged()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HNReaderTheme {
        NewsItemRow(
            newsItem = HNStoryEntity(
                id = "1223",
                by = "Jesse",
                title = "A title",
                url = ""
            )
        )
    }
}