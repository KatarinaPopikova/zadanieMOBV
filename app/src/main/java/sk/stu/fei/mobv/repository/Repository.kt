package sk.stu.fei.mobv.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import sk.stu.fei.mobv.database.daos.BarDao
import sk.stu.fei.mobv.database.entities.asDomainModelList
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.network.RestApiService
import sk.stu.fei.mobv.network.UserCreateBody
import sk.stu.fei.mobv.network.UserLoginBody
import sk.stu.fei.mobv.network.dtos.UserDto
import sk.stu.fei.mobv.network.dtos.asDomainModel
import sk.stu.fei.mobv.network.dtos.asEntityModelList
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
            val response = service.registerUser(UserCreateBody(name = name, password = password))
            if (response.isSuccessful) {
                response.body()?.let { user ->
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
            val response = service.loginUser(UserLoginBody(name = name, password = password))
            if (response.isSuccessful) {
                response.body()?.let { user ->
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

    suspend fun refreshBarList(
        onError: (error: String) -> Unit
    ) {
        try {
            val response = service.getBars()
            if (response.isSuccessful) {
                response.body()?.let { bars ->
                    barDao.insertBars(bars.asEntityModelList())
                } ?: onError("Failed to load bars")
            } else {
                onError("Failed to read bars")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load bars, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load bars, error.")
        }
    }

    suspend fun getTagBar(
        id: Long,
        onError: (error: String) -> Unit
    ): Bar? {
        var bar: Bar? = null
        try {
            val query = "[out:json];node($id);out body;>;out skel;"
            val response = service.getTagBars(query)
            if (response.isSuccessful) {
                response.body()?.let { tagBars ->
                    if (tagBars.tagBarList.isNotEmpty()) {
                        bar = tagBars.tagBarList[0].asDomainModel()
                    }
                } ?: onError("Failed to load bars")
            } else {
                onError("Failed to read bars")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load bars, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load bars, error.")
        }
        return bar
    }

    fun getBarsByNameAsc(): LiveData<List<Bar>> =
        Transformations.map(barDao.getBarsByNameAsc().asLiveData()) {
            it.asDomainModelList()
        }

    fun getBarsByNameDesc(): LiveData<List<Bar>> =
        Transformations.map(barDao.getBarsByNameDesc().asLiveData()) {
            it.asDomainModelList()
        }

    fun getBarsByUsersCountAsc(): LiveData<List<Bar>> =
        Transformations.map(barDao.getBarsByUsersCountAsc().asLiveData()) {
            it.asDomainModelList()
        }

    fun getBarsByUsersCountDesc(): LiveData<List<Bar>> =
        Transformations.map(barDao.getBarsByUsersCountDesc().asLiveData()) {
            it.asDomainModelList()
        }

    suspend fun getBarUserCount(barId: Long): Int = barDao.getBarUserCount(barId)


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