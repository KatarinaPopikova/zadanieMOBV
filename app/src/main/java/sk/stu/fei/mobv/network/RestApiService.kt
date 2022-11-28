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
import retrofit2.http.Body
import retrofit2.http.POST
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

interface RestApiService {
    @POST("user/create.php")
    suspend fun userCreate(@Body user: UserCreateBody): Response<UserDto>

    @POST("user/login.php")
    suspend fun userLogin(@Body user: UserLoginBody): Response<UserDto>

    @POST("user/refresh.php")
    fun userRefresh(@Body user: UserRefreshBody) : Call<UserDto>

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