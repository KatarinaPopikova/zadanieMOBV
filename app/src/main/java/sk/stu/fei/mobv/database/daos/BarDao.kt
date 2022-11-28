package sk.stu.fei.mobv.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sk.stu.fei.mobv.database.entities.BarEntity

@Dao
interface BarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBars(bars: List<BarEntity>)

    @Query("DELETE FROM bars")
    suspend fun deleteBars()

    @Query("SELECT * FROM bars order by users DESC, name ASC")
    fun getBars(): LiveData<List<BarEntity>?>
}