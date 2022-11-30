package sk.stu.fei.mobv.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sk.stu.fei.mobv.database.entities.BarEntity

@Dao
interface BarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBars(bars: List<BarEntity>)

    @Query("DELETE FROM bars")
    suspend fun deleteBars()

    @Query("SELECT * FROM bars order by name ASC")
    fun getBarsByNameAsc(): Flow<List<BarEntity>>

    @Query("SELECT * FROM bars order by name DESC")
    fun getBarsByNameDesc(): Flow<List<BarEntity>>

    @Query("SELECT * FROM bars order by name ASC")
    fun getBarsByDistanceAsc(): Flow<List<BarEntity>>

    @Query("SELECT * FROM bars order by name DESC")
    fun getBarsByDistanceDesc(): Flow<List<BarEntity>>

    @Query("SELECT * FROM bars order by users_count ASC")
    fun getBarsByUsersCountAsc(): Flow<List<BarEntity>>

    @Query("SELECT * FROM bars order by users_count DESC")
    fun getBarsByUsersCountDesc(): Flow<List<BarEntity>>
}