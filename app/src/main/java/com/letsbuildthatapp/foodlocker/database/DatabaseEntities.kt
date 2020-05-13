package com.letsbuildthatapp.foodlocker.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.letsbuildthatapp.foodlocker.models.PostProperty

/**
 * DatabasePost represents a post entity in the database.
 */
@Entity
data class DatabasePost constructor(
        @PrimaryKey
        val id: Int?,
        val content: String,
        val photo: String?,
        var likes: Int?,
        val username: String,
        val createdAt: String,
        val userId: String,
        val photoProfile: String)

/**
 * Map DatabasePosts to domain entities
 */
fun List<DatabasePost>.asDomainModel(): List<PostProperty> {
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