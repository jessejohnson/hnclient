package com.jessejojojohnson.hnreader.network

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
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
        val l = hnService.listTopStories().execute().body()
        Log.d("HNReader", "Response size is ${l?.size}")
        Log.d("HNReader", "First response is ${l?.first()}")
        l?.first()?.let {
            val s = HNService.get().resolveItem(it).execute().body()
            Log.d("HNReader", "First story with id ${s?.id} is by ${s?.by}")
            Log.d("HNReader", "${s?.title} --> ${s?.url}")
        }
        return Result.success()
    }

}
