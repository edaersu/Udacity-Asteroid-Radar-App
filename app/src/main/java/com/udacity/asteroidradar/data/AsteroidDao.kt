package com.udacity.asteroidradar.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    //get all asteroids
    @Query("select * from DatabaseAsteroid order by closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    //Get asteroid data today from cache and return list
    @Query("select * from DatabaseAsteroid where closeApproachDate = :today order by closeApproachDate")
    fun getAsteroidsToday(today: String): LiveData<List<DatabaseAsteroid>>

    //get asteroids from a week
    @Query("select * from DatabaseAsteroid where closeApproachDate between :today and :endDate order by closeApproachDate ASC")
    fun getAsteroidsWeek(today: String, endDate: String): LiveData<List<DatabaseAsteroid>>

    //add asteroids to cache overwriting conflicting data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: DatabaseAsteroid)

    //delete asteroids from previous day
    @Query("delete from DatabaseAsteroid where closeApproachDate between :yesterday and :pastDate")
    fun deletePreviousAsteroids(yesterday: String, pastDate: String)
}

//Picture Of the Day (POD) dao
@Dao
interface PodDao{

    @Query("select * from DatabasePictureOfDay")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOD(pictureOfDay: DatabasePictureOfDay)

}

/* Room Db for asteroid objects */
@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1,exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    //set up DAOs
    abstract val asteroidDao: AsteroidDao
    abstract val podDao: PodDao

}
//db singleton instance
private lateinit var INSTANCE: AsteroidDatabase

//create db
fun getDatabase(context: Context): AsteroidDatabase {

    //ensure serial data flow
    synchronized(AsteroidDatabase::class.java) {
        //check if singleton has been initialized
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java, "asteroids"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}