package com.letsbuildthatapp.foodlocker.network

import com.letsbuildthatapp.foodlocker.database.DatabasePost
import com.letsbuildthatapp.foodlocker.models.PostProperty
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkPost(
        val id: Int?,
        val content: String,
        val photo: String?,
        var likes: Int?,
        val username: String,
        @Json(name = "created_at") val createdAt: String,
        @Json(name = "user_id") val userId: String,
        @Json(name = "photo_profile") val photoProfile: String)

/**
 * Convert Network results to database objects
 */
fun List<NetworkPost>.asDomainModel(): List<PostProperty> {
    return map {
        PostProperty(
                id = it.id,
                content = it.content,
                photo = it.photo,
                likes = it.likes,
                username = it.username,
                createdAt = it.createdAt,
                userId = it.userId,
                photoProfile = it.photoProfile)
    }
}

/**
 * Convert Network results to database objects
 */
fun List<NetworkPost>.asDatabaseModel(): List<DatabasePost> {
    return map {
        DatabasePost(
                id = it.id,
                content = it.content,
                photo = it.photo,
                likes = it.likes,
                username = it.username,
                createdAt = it.createdAt,
                userId = it.userId,
                photoProfile = it.photoProfile)
    }
}

