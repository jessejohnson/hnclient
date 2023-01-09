package com.jessejojojohnson.hnreader.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.*
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import org.jsoup.Jsoup
import org.koin.java.KoinJavaComponent.get
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface HNService {
    @GET("v0/topstories.json")
    fun listTopStories(): Call<List<String>>

    @GET("v0/item/{itemId}.json")
    fun resolveItem(@Path("itemId") id: String): Call<StoryResponse>

    companion object {
        fun get(): HNService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(HNService::class.java)
        }
    }
}

data class StoryResponse(
    val id: String,
    val by: String,
    val title: String,
    val url: String
)

class GetStoriesWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val hnService: HNService = get(HNService::class.java)
        val db: AppDatabase = get(AppDatabase::class.java)
        val workManager: WorkManager = get(WorkManager::class.java)
        val l = hnService.listTopStories().execute().body()

        db.clearAllTables() // empty the cache first!

        l?.take(20)?.forEach {
            val s = HNService.get().resolveItem(it).execute().body()
            val entity = HNStoryEntity(
                id = s?.id ?: "NONE",
                by = s?.by ?: "NONE",
                url = s?.url ?: "NONE",
                title = s?.title ?: "NONE",
                content = "NONE"
            )
            db.hnEntityDao().insert(entity)

            //start a new Worker to download content at the same time :)
            workManager.enqueue(
                OneTimeWorkRequestBuilder<GetWebContentWorker>()
                    .setInputData(
                        workDataOf(
                            GetWebContentWorker.URL to entity.url,
                            GetWebContentWorker.ITEM_ID to entity.id
                        )
                    )
                    .build()
            )
        }
        return Result.success()
    }
}

class GetWebContentWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        val articleKey = stringPreferencesKey("article")
        const val URL = "url"
        const val ITEM_ID = "itemId"
    }

    override suspend fun doWork(): Result {
        val db: AppDatabase = get(AppDatabase::class.java)
        val ds: DataStore<Preferences> = get(DataStore::class.java)
        val url = inputData.getString(URL) ?: return Result.failure()
        val itemId = inputData.getString(ITEM_ID) ?: return Result.failure()

        val webPage = Jsoup.connect(url).get()
        val content = webPage.body().wholeText()

        val newsItem = db.hnEntityDao().get(itemId)
        val n = newsItem.copy(content = content)
        db.hnEntityDao().update(n)

        ds.edit {
            it[articleKey] = content
        }

        return Result.success()
    }
}
