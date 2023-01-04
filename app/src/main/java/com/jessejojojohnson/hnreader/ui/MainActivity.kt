package com.jessejojojohnson.hnreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import com.jessejojojohnson.hnreader.ui.detail.NewsItemDetailScreen
import com.jessejojojohnson.hnreader.ui.home.HomeScreen
import com.jessejojojohnson.hnreader.ui.home.NewsItemRow
import com.jessejojojohnson.hnreader.ui.theme.HNReaderTheme

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
    startDestination: String = AppRoute.Home.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen(onItemClick = {
                navController.navigate(AppRoute.NewsItemDetail.route + it)
            })
        }
        composable(
            AppRoute.NewsItemDetail.route + "{${AppRoute.ITEM_ID}}",
            arguments = listOf(navArgument(AppRoute.ITEM_ID) { type = NavType.StringType})
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(AppRoute.ITEM_ID)?.let {
                NewsItemDetailScreen(itemId = it)
            }
        }
    }
}

sealed class AppRoute(val route: String) {
    object Home: AppRoute("home")
    object NewsItemDetail: AppRoute("newsItem/")
    companion object {
        const val ITEM_ID = "itemId"
    }
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