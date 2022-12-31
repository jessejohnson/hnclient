package com.jessejojojohnson.hnreader.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jessejojojohnson.hnreader.data.AppDatabase
import com.jessejojojohnson.hnreader.data.HNStoryEntity
import org.koin.java.KoinJavaComponent.get
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface HNService {
    @GET("v0/topstories.json")
    fun listTopStories() : Call<List<String>>

    @GET("v0/item/{itemId}.json")
    fun resolveItem(@Path("itemId") id: String) : Call<StoryResponse>

    companion object {
        fun get() : HNService {
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
        val l = hnService.listTopStories().execute().body()

        db.clearAllTables() // empty the cache first!

        l?.take(10)?.forEach {
            val s = HNService.get().resolveItem(it).execute().body()
            val entity = HNStoryEntity(
                id = s?.id ?: "NONE",
                by = s?.by ?: "NONE",
                url = s?.url ?: "NONE",
                title = s?.title ?: "NONE"
            )
            db.hnEntityDao().insert(entity)
        }
        return Result.success()
    }

}
