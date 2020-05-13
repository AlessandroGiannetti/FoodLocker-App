package com.letsbuildthatapp.foodlocker.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.letsbuildthatapp.foodlocker.database.PostsDatabase
import com.letsbuildthatapp.foodlocker.database.asDomainModel
import com.letsbuildthatapp.foodlocker.models.PostProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.PostApi
import com.letsbuildthatapp.foodlocker.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "POST REPOSITORY"

class PostsRepository(private val database: PostsDatabase) {

    val posts: LiveData<List<PostProperty>> = Transformations.map(database.postDao.getPosts()) {
        it.asDomainModel()
    }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            Log.e(TAG, "refresh videos is called")
            val playlist = PostApi.retrofitServiceAllPost.getProperties(FirebaseDBMng.auth.currentUser?.uid.toString())
                    .await()
            database.postDao.insertAll(playlist.asDatabaseModel())
        }
    }

    suspend fun dropPosts() {
        withContext(Dispatchers.IO) {
            Log.e(TAG, "drop videos is called")
            database.postDao.deleteAll()
        }
    }
}