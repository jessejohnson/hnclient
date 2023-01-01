package com.jessejojojohnson.hnreader.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

class HNDBService {
    companion object {
        fun get(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "hn-reader-db"
            ).build()
        }
    }
}

@Entity
data class HNStoryEntity(
    @PrimaryKey val id: String,
    val by: String,
    val title: String,
    val url: String
) {
    companion object {
        val default = HNStoryEntity("", "", "", "")
    }
}

@Dao
interface HNStoryEntityDao {

    @Query("SELECT * from hnstoryentity")
    fun getAll() : List<HNStoryEntity>

    @Query("SELECT * from hnstoryentity")
    fun getAllFlow() : Flow<List<HNStoryEntity>>

    @Query("SELECT * from hnstoryentity WHERE id = :id")
    fun get(id: String) : HNStoryEntity

    @Query("SELECT * from hnstoryentity WHERE id = :id")
    fun getFlow(id: String) : Flow<HNStoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg stories: HNStoryEntity)

    @Delete
    fun delete(vararg stories: HNStoryEntity)
}

@Database(entities = [HNStoryEntity::class], exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hnEntityDao() : HNStoryEntityDao
}