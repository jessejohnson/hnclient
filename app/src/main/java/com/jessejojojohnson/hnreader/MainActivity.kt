package com.jessejojojohnson.hnreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import com.jessejojojohnson.hnreader.network.GetStoriesWorker
import com.jessejojojohnson.hnreader.network.GetWebContentWorker
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
                    MainScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(onItemClick = {
                navController.navigate("newsItem/$it")
            })
        }
        composable(
        "newsItem/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType})
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString("itemId")?.let { NewsItemDetailScreen(itemId = it) }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(),
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
fun NewsItemDetailScreen(
    viewModel: NewsItemDetailViewModel = viewModel(),
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
            Text(text = newsItem.value.title)
        }
        item {
            Text(text = newsItem.value.content)
        }
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

class NewsItemDetailViewModel : ViewModel() {
    private val db: AppDatabase = get(AppDatabase::class.java)

    fun getItem(itemId: String) = db.hnEntityDao().get(itemId)
    fun getItemFlow(itemId: String) = db.hnEntityDao().getFlow(itemId).distinctUntilChanged()
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
                url = "",
                content = ""
            ),
            onItemClick = {}
        )
    }
}