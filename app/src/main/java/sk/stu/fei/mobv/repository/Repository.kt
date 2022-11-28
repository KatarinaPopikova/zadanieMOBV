package sk.stu.fei.mobv.repository

import sk.stu.fei.mobv.database.daos.BarDao
import sk.stu.fei.mobv.network.RestApiService
import sk.stu.fei.mobv.network.UserCreateBody
import sk.stu.fei.mobv.network.UserLoginBody
import sk.stu.fei.mobv.network.dtos.UserDto
import java.io.IOException

class Repository private constructor(
    private val service: RestApiService,
    private val barDao: BarDao
) {
    suspend fun registerUser(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserDto?) -> Unit
    ) {
        try {
            val resp = service.userCreate(UserCreateBody(name = name, password = password))
            if (resp.isSuccessful) {
                resp.body()?.let { user ->
                    if (user.id == "-1") {
                        onStatus(null)
                        onError("Name already exists. Choose another.")
                    } else {
                        onStatus(user)
                    }
                }
            } else {
                onError("Failed to sign up, try again later.")
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Sign up failed, check internet connection")
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Sign up failed, error.")
            onStatus(null)
        }
    }

    suspend fun loginUser(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserDto?) -> Unit
    ) {
        try {
            val resp = service.userLogin(UserLoginBody(name = name, password = password))
            if (resp.isSuccessful) {
                resp.body()?.let { user ->
                    if (user.id == "-1") {
                        onStatus(null)
                        onError("Wrong name or password.")
                    } else {
                        onStatus(user)
                    }
                }
            } else {
                onError("Failed to login, try again later.")
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Login failed, check internet connection")
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Login in failed, error.")
            onStatus(null)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(service: RestApiService, barDao: BarDao): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(service, barDao).also { INSTANCE = it }
            }
    }
}