package com.letsbuildthatapp.foodlocker.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDao {
    @Query("select * from databasepost order by databasepost.id desc ")
    fun getPosts(): LiveData<List<DatabasePost>>

    @Query("delete from databasepost ")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DatabasePost>)
}


@Database(entities = [DatabasePost::class], version = 1)
abstract class PostsDatabase : RoomDatabase() {
    abstract val postDao: PostDao
}

private lateinit var INSTANCE: PostsDatabase

fun getDatabase(context: Context): PostsDatabase {
    synchronized(PostsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    PostsDatabase::class.java,
                    "posts").build()
        }
    }
    return INSTANCE
}
