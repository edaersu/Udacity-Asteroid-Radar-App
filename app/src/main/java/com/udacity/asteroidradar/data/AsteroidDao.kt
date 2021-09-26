package com.udacity.asteroidradar.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("select * from DatabaseAsteroid order by closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from DatabaseAsteroid where closeApproachDate = :today order by closeApproachDate")
    fun getAsteroidsToday(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from DatabaseAsteroid where closeApproachDate between :today and :endDate order by closeApproachDate ASC")
    fun getAsteroidsWeek(today: String, endDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("delete from DatabaseAsteroid where closeApproachDate between :yesterday and :pastDate")
    fun deletePreviousAsteroids(yesterday: String, pastDate: String)
}

@Dao
interface PodDao{

    @Query("select * from DatabasePictureOfDay")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOD(pictureOfDay: DatabasePictureOfDay)

}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1,exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {

    abstract val asteroidDao: AsteroidDao
    abstract val podDao: PodDao

}
private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {

    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java, "asteroids"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}