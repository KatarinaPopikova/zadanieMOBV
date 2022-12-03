package sk.stu.fei.mobv

import android.app.Application
import sk.stu.fei.mobv.database.AppDatabase
import sk.stu.fei.mobv.network.RestApiService
import sk.stu.fei.mobv.repository.Repository

class MainApplication : Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val restApiService: RestApiService by lazy { RestApiService.create(this) }
    val repository by lazy { Repository.getInstance(restApiService, database.barDao(), database.myFriendDao()) }
}