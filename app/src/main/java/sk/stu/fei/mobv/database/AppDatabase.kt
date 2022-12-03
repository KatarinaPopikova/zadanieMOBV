package sk.stu.fei.mobv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sk.stu.fei.mobv.database.daos.BarDao
import sk.stu.fei.mobv.database.daos.MyFriendDao
import sk.stu.fei.mobv.database.entities.BarEntity
import sk.stu.fei.mobv.database.entities.MyFriendEntity

@Database(entities = [BarEntity::class, MyFriendEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barDao(): BarDao
    abstract fun myFriendDao(): MyFriendDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mobv_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}