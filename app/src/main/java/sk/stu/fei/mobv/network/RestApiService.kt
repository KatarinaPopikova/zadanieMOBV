package sk.stu.fei.mobv.network

import android.content.Context
import com.example.zadanie.data.api.helper.AuthInterceptor
import sk.stu.fei.mobv.network.helpers.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import sk.stu.fei.mobv.network.dtos.BarDto
import sk.stu.fei.mobv.network.dtos.FriendDto
import sk.stu.fei.mobv.network.dtos.TagBarContainerDto
import sk.stu.fei.mobv.network.dtos.UserDto

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

data class UserRefreshBody(
    val refresh: String
)

data class UserCreateBody(
    val name: String,
    val password: String
)

data class UserLoginBody(
    val name: String,
    val password: String
)

data class BarMessageBody(
    val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double
)

data class FriendMessageBody(
    val contact: String,
)

interface RestApiService {
    @POST("user/create.php")
    suspend fun registerUser(@Body user: UserCreateBody): Response<UserDto>

    @POST("user/login.php")
    suspend fun loginUser(@Body user: UserLoginBody): Response<UserDto>

    @POST("user/refresh.php")
    fun refreshUser(@Body user: UserRefreshBody) : Call<UserDto>

    @GET("bar/list.php")
    @Headers("mobv-auth: accept")
    suspend fun getBars() : Response<List<BarDto>>

    @GET("https://overpass-api.de/api/interpreter?")
    suspend fun getTagBars(@Query("data") data: String): Response<TagBarContainerDto>

    @POST("bar/message.php")
    @Headers("mobv-auth: accept")
    suspend fun barMessage(@Body bar: BarMessageBody) : Response<Any>

    @GET("contact/list.php")
    @Headers("mobv-auth: accept")
    suspend fun getFriends() : Response<List<FriendDto>>

    @GET("contact/friends.php")
    @Headers("mobv-auth: accept")
    suspend fun getMyFriends() : Response<List<FriendDto>>

    @POST("contact/message.php")
    @Headers("mobv-auth: accept")
    suspend fun addFriend(@Body friend: FriendMessageBody) : Response<Any>

    @POST("contact/delete.php")
    @Headers("mobv-auth: accept")
    suspend fun deleteFriend(@Body friend: FriendMessageBody) : Response<Any>

    companion object{
        const val BASE_URL = "https://zadanie.mpage.sk/"

        fun create(context: Context): RestApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(RestApiService::class.java)
        }
    }
}