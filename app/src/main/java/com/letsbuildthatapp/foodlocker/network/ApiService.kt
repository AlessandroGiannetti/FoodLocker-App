/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.letsbuildthatapp.foodlocker.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.letsbuildthatapp.foodlocker.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val TAG = "API SERVICE"
private const val BASE_URL = "" //BASE URL
/*private val tokenID = FirebaseDBMng.getTokenID()

// assignment of the firebase token inside the header in the "Authorization" field
private var client = OkHttpClient.Builder().addInterceptor { chain ->
    val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", tokenID)
            .build()
    chain.proceed(newRequest) }.build()*/

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
//        .client(client)
        .baseUrl(BASE_URL)
        .build()

/*
* POSTS MANAGEMENT
* */
interface AllPostApiService {
    @GET("users/{uuid}/followings_posts")
    fun getProperties(@Path("uuid") uuid: String):
            Deferred<List<NetworkPost>>
}

interface PersonalPostApiService {
    @GET("users/{uuid}/posts")
    fun getPersonalPosts(@Path("uuid") uuid: String):
            Deferred<List<PostProperty>>
}

interface LikePostApiService {
    @PUT("users/{uuid}/followings_posts/{idPost}")
    fun setLike(@Path("uuid") uuid: String, @Path("idPost") idPost: Int):
            Deferred<PostProperty>
}

interface PostApiService {
    @POST("users/{uuid}/posts")
    @FormUrlEncoded
    fun setPost(@Path("uuid") uuid: String,
                @Field("content") content: String,
                @Field("photo") photo: String?,
                @Field("likes") likes: Int):
            Deferred<PostProperty>
}

/*
* USERS SEARCH MANAGEMENT
* */
interface SetUserApiService {
    @POST("users/")
    @FormUrlEncoded
    fun setUser(@Field("id") uuid: String,
                @Field("username") username: String,
                @Field("photo_profile") photoProfile: String):
            Deferred<UserProperty>
}

interface GetUsersApiService {
    @GET("users/{uuid}/researches/{username}")
    fun getUsers(@Path("uuid") uuid: String,
                 @Path("username") username: String):
            Deferred<List<UserProperty>>
}

/*
* RELATIONSHIPS MANAGEMENT
* */
interface GetFollowedApiService {
    @GET("users/{uuid}/followings")
    fun getFollowed(@Path("uuid") uuid: String):
            Deferred<List<UserProperty>>
}

interface GetFollowersApiService {
    @GET("users/{uuid}/followers")
    fun getFollowers(@Path("uuid") uuid: String):
            Deferred<List<UserProperty>>
}

interface SetFollowedApiService {
    @POST("users/{uuid}/followings")
    @FormUrlEncoded
    fun setFollowed(@Path("uuid") uuid: String,
                    @Field("followed_id") followedId: String):
            Deferred<UserProperty>
}

interface RemoveFollowerApiService {
    @DELETE("users/{uuid}/followers/{follower_id}")
    fun removeFollower(@Path("uuid") uuid: String,
                       @Path("follower_id") followerId: String):
            Deferred<UserProperty>
}

interface RemoveFollowedApiService {
    @DELETE("users/{uuid}/followings/{followed_id}")
    fun removeFollowed(@Path("uuid") uuid: String,
                       @Path("followed_id") followedId: String):
            Deferred<UserProperty>
}

/*
* FOOD MANAGEMENT
* */
interface GetFoodsByIdApiService {
    @GET("foods_id/{id}")
    fun getFoodById(@Path("id") id: String):
            Deferred<FoodProperty>
}

interface GetFoodsByNameApiService {
    @GET("foods/{name}")
    fun getFoodByName(@Path("name") name: String):
            Deferred<List<FoodProperty>>
}

interface GetDiaryFoodsApiService {
    @GET("users/{uuid}/days/{day}/food_days")
    fun getDiaryFood(@Path("uuid") uuid: String,
                     @Path("day") day: String):
            Deferred<List<FoodProperty>>
}

interface SetFoodOnDiaryApiService {
    @POST("users/{uuid}/days/{day}/food_days")
    @FormUrlEncoded
    fun setFoodOnDiary(@Path("uuid") uuid: String,
                       @Path("day") day: String,
                       @Field("food_id") foodId: String,
                       @Field("meal") meal: String):
            Deferred<FoodProperty>
}

/*
* SPORT MANAGEMENT
* */
interface GetSportsByIdApiService {
    @GET("sports_id/{id}")
    fun getSportById(@Path("id") id: String):
            Deferred<SportProperty>
}

interface GetSportsByNameApiService {
    @GET("sports/{name}")
    fun getSportsByName(@Path("name") name: String):
            Deferred<List<SportProperty>>
}

interface GetDiarySportsApiService {
    @GET("users/{uuid}/days/{day}/sport_days")
    fun getDiarySport(@Path("uuid") uuid: String,
                      @Path("day") day: String):
            Deferred<List<SportProperty>>
}

interface SetSportOnDiaryApiService {
    @POST("users/{uuid}/days/{day}/sport_days")
    @FormUrlEncoded
    fun setSportOnDiary(@Path("uuid") uuid: String,
                        @Path("day") day: String,
                        @Field("sport_id") foodId: String,
                        @Field("hour") hour: String):
            Deferred<SportProperty>
}

/*
* DAY MANAGEMENT
* */
interface SetDayApiService {
    @POST("users/{uuid}/days/")
    @FormUrlEncoded
    fun setDay(@Path("uuid") uuid: String,
               @Field("date") date: String,
               @Field("water") water: Int,
               @Field("note") note: String):
            Deferred<DayProperty>
}

interface GetDayApiService {
    @GET("users/{uuid}/days/{day}")
    fun getDay(@Path("uuid") uuid: String,
               @Path("day") day: String):
            Deferred<DayProperty>
}

/*
* WEIGHT MANAGEMENT
* */
interface GetWeightsApiService {
    @GET("users/{uuid}/weights")
    fun getWeights(@Path("uuid") uuid: String):
            Deferred<List<WeightProperty>>
}

interface SetWeightApiService {
    @POST("users/{uuid}/weights")
    @FormUrlEncoded
    fun setWeight(@Path("uuid") uuid: String,
                  @Field("weight") weight: String,
                  @Field("photo") photo: String?):
            Deferred<WeightProperty>
}


object PostApi {
    val retrofitServiceAllPost: AllPostApiService by lazy {
        retrofit.create(AllPostApiService::class.java)
    }
    val retrofitServicePersonalPost: PersonalPostApiService by lazy {
        retrofit.create(PersonalPostApiService::class.java)
    }
    val retrofitServiceLikePost: LikePostApiService by lazy {
        retrofit.create(LikePostApiService::class.java)
    }
    val retrofitServiceSetPost: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}

object WeightApi {
    val retrofitServiceAllWeights: GetWeightsApiService by lazy {
        retrofit.create(GetWeightsApiService::class.java)
    }
    val retrofitServiceSetWeight: SetWeightApiService by lazy {
        retrofit.create(SetWeightApiService::class.java)
    }
}

object FoodApi {
    val retrofitServiceGetDiaryFoods: GetDiaryFoodsApiService by lazy {
        retrofit.create(GetDiaryFoodsApiService::class.java)
    }
    val retrofitServiceGetFoodsByName: GetFoodsByNameApiService by lazy {
        retrofit.create(GetFoodsByNameApiService::class.java)
    }
    val retrofitServiceGetFoodsById: GetFoodsByIdApiService by lazy {
        retrofit.create(GetFoodsByIdApiService::class.java)
    }
    val retrofitServiceSetFoodOnDiary: SetFoodOnDiaryApiService by lazy {
        retrofit.create(SetFoodOnDiaryApiService::class.java)
    }
}

object SportApi {
    val retrofitServiceGetDiarySports: GetDiarySportsApiService by lazy {
        retrofit.create(GetDiarySportsApiService::class.java)
    }
    val retrofitServiceGetSportsByName: GetSportsByNameApiService by lazy {
        retrofit.create(GetSportsByNameApiService::class.java)
    }
    val retrofitServiceGetSportsById: GetSportsByIdApiService by lazy {
        retrofit.create(GetSportsByIdApiService::class.java)
    }
    val retrofitServiceSetSportOnDiary: SetSportOnDiaryApiService by lazy {
        retrofit.create(SetSportOnDiaryApiService::class.java)
    }
}


object DayApi {
    val retrofitServiceSetDay: SetDayApiService by lazy {
        retrofit.create(SetDayApiService::class.java)
    }

    val retrofitServiceGetDay: GetDayApiService by lazy {
        retrofit.create(GetDayApiService::class.java)
    }
}

object UserApi {
    val retrofitServiceGetUsers: GetUsersApiService by lazy {
        retrofit.create(GetUsersApiService::class.java)
    }

    val retrofitServiceSetUser: SetUserApiService by lazy {
        retrofit.create(SetUserApiService::class.java)
    }

    val retrofitServiceGetFollowers: GetFollowersApiService by lazy {
        retrofit.create(GetFollowersApiService::class.java)
    }

    val retrofitServiceGetFollowed: GetFollowedApiService by lazy {
        retrofit.create(GetFollowedApiService::class.java)
    }

    val retrofitServiceSetFollowed: SetFollowedApiService by lazy {
        retrofit.create(SetFollowedApiService::class.java)
    }

    val retrofitServiceRemoveFollowed: RemoveFollowedApiService by lazy {
        retrofit.create(RemoveFollowedApiService::class.java)
    }

    val retrofitServiceRemoveFollower: RemoveFollowerApiService by lazy {
        retrofit.create(RemoveFollowerApiService::class.java)
    }
}
