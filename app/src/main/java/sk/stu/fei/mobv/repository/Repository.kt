package sk.stu.fei.mobv.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import sk.stu.fei.mobv.database.daos.BarDao
import sk.stu.fei.mobv.database.daos.MyFriendDao
import sk.stu.fei.mobv.database.entities.asDomainModelList
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.domain.Friend
import sk.stu.fei.mobv.domain.MyLocation
import sk.stu.fei.mobv.network.*
import sk.stu.fei.mobv.network.dtos.*
import java.io.IOException

class Repository private constructor(
    private val service: RestApiService,
    private val barDao: BarDao,
    private val myFriendDao: MyFriendDao
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
                    barDao.deleteBars()
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

    suspend fun getBar(
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

    suspend fun getNearbyBars(
        myLatitude: Double, myLongitude: Double,
        onError: (error: String) -> Unit
    ): List<Bar> {
        var bars = listOf<Bar>()
        try {
            val query =
                "[out:json];node(around:250,$myLatitude,$myLongitude);(node(around:250)[\"amenity\"~\"^pub$|^bar$|^restaurant$|^cafe$|^fast_food$|^stripclub$|^nightclub$\"];);out body;>;out skel;"
            val resp = service.getTagBars(query)
            if (resp.isSuccessful) {
                resp.body()?.let { tagBars ->
                    bars = tagBars.tagBarList.map {
                        it.asDomainModel().apply {
                            distance = getDistanceTo(myLatitude, myLongitude)
                        }
                    }
                    bars = bars.filter { it.name.isNotBlank() }.sortedBy { it.distance }
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
        return bars
    }

    suspend fun checkInBar(
        bar: Bar,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        try {
            val resp = service.barMessage(
                BarMessageBody(
                    bar.id.toString(),
                    bar.name,
                    bar.type,
                    bar.latitude,
                    bar.longitude
                )
            )
            if (resp.isSuccessful) {
                onSuccess(true)
            } else {
                onError("Failed to login, try again later.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Login failed, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Login in failed, error.")
        }
    }

    suspend fun addFriend(
        friendName: String,
        onError: (error: String) -> Unit,
        onSuccess: (success: String) -> Unit
    ) {
        try {
            val resp = service.addFriend(
                FriendMessageBody(
                    friendName
                )
            )
            if (resp.isSuccessful) {
                onSuccess("Friend was succesfully added")
                refreshMyFriendsList { onError(it) }
            } else {
                onError("Failed to login, try again later.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Login failed, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Login in failed, error.")
        }
    }

    suspend fun deleteMyFriend(
        myFriend: Friend,
        onError: (error: String) -> Unit,
        onSuccess: (success: String) -> Unit
    ) {
        try {
            val resp = service.deleteFriend(
                FriendMessageBody(
                    myFriend.name
                )
            )
            if (resp.isSuccessful) {
                onSuccess("Friend was succesfully added")
                myFriendDao.deleteMyFriend(myFriend.asEntityModel())
            } else {
                onError("Failed to login, try again later.")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(ex.message.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Login in failed, error.")
        }
    }

    suspend fun getFriends(
        onError: (error: String) -> Unit
    ): List<Friend> {
        var friends = listOf<Friend>()
        try {
            val response = service.getFriends()
            if (response.isSuccessful) {
                response.body()?.let { friendsDto ->
                    friends = friendsDto.asDomainModelList()
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
        return friends
    }

    suspend fun refreshMyFriendsList(
        onError: (error: String) -> Unit
    ) {
        try {
            val response = service.getMyFriends()
            if (response.isSuccessful) {
                response.body()?.let { myFriends ->
                    myFriendDao.deleteMyFriends()
                    myFriendDao.insertMyFriends(myFriends.asEntityModelList())
                } ?: onError("Failed to load friends")
            } else {
                onError("Failed to read friends")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError("Failed to load friends, check internet connection")
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError("Failed to load friends, error.")
        }
    }


    fun getMyFriends(): LiveData<List<Friend>> =
        Transformations.map(myFriendDao.getMyFriends().asLiveData()) {
            it.asDomainModelList()
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

    fun getBarsByDistanceAsc(myLocation: MyLocation?): LiveData<List<Bar>> =
        Transformations.map(barDao.getBarsByNameAsc().asLiveData()) { barEntity ->
            var bars = barEntity.asDomainModelList()
            myLocation?.let {
                bars = bars.map { bar ->
                    bar.distance = bar.getDistanceTo(it.latitude, it.longitude)
                    bar
                }.sortedBy { it.distance }
            }
            bars
        }

    fun getBarsByDistanceDesc(myLocation: MyLocation?): LiveData<List<Bar>> =
        Transformations.map(barDao.getBarsByNameAsc().asLiveData()) { barEntity ->
            var bars = barEntity.asDomainModelList()
            myLocation?.let {
                bars = bars.map { bar ->
                    bar.distance = bar.getDistanceTo(it.latitude, it.longitude)
                    bar
                }.sortedByDescending { it.distance }
            }
            bars
        }

    suspend fun getBarUserCount(barId: Long): Int = barDao.getBarUserCount(barId)


    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(
            service: RestApiService,
            barDao: BarDao,
            myFriendDao: MyFriendDao
        ): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(service, barDao, myFriendDao).also { INSTANCE = it }
            }
    }
}