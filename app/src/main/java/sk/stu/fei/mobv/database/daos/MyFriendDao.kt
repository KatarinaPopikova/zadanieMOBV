package sk.stu.fei.mobv.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sk.stu.fei.mobv.database.entities.MyFriendEntity

@Dao
interface MyFriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyFriends(myFriends: List<MyFriendEntity>)

    @Query("DELETE FROM my_friends")
    suspend fun deleteMyFriends()

    @Delete
    suspend fun deleteMyFriend(myFriendEntity: MyFriendEntity)

    @Query("SELECT * FROM my_friends order by name ASC")
    fun getMyFriends(): Flow<List<MyFriendEntity>>

}