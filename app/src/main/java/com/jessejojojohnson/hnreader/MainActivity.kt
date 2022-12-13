package com.jessejojojohnson.hnreader

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jessejojojohnson.hnreader.network.GetStoriesWorker
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
                    Greeting("Android")
                    MyButton(modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth())
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun MyButton(modifier: Modifier = Modifier.wrapContentSize()) {
    Button(
        modifier = modifier,
        onClick = {
            Log.d("TestApp", "Button clicked!")
            Log.wtf("TestApp", "Button clicked!")
            WorkManager.getInstance()
                .enqueue(
                    OneTimeWorkRequestBuilder<GetStoriesWorker>().build()
                )
        }) {
        Text(text = "Click Me!")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HNReaderTheme {
        Greeting("Android")
    }
}