package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.asDomainModel
import com.udacity.asteroidradar.data.asDomainModelPicture
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.NetworkAsteroid
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository (private val database: AsteroidDatabase){

    //Asteroid service object
    var asteroidApiService = Network.retrofitService

    /* transform Asteroid database LiveData to Asteroid Live data */

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroidsToday(
        getToday()
    )) {
        it?.asDomainModel()
    }
    val asteroidsWeek: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroidsWeek(
        getToday(), getEndDay()
    )) {
        it?.asDomainModel()
    }

    val asteroidSaved: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()){
            it?.asDomainModel()
        }

    /* Transform database picture of day to domain object*/
    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.podDao.getPictureOfDay()){
            it?.asDomainModelPicture()
        }

    //Refresh asteroids
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO){ //force switch to IO dispatcher
            val jsonResult = asteroidApiService.getAsteroids(getToday(), getEndDay(), API_KEY)
            val asteroids = parseAsteroidsJsonResult(JSONObject(jsonResult))
            val networkAsteroidList = asteroids.map {
                NetworkAsteroid(
                    it.id,
                    it.codename,
                    it.closeApproachDate,
                    it.absoluteMagnitude,
                    it.estimatedDiameter,
                    it.relativeVelocity,
                    it.distanceFromEarth,
                    it.isPotentiallyHazardous
                )
            }
            database.asteroidDao.insertAll(*networkAsteroidList.asDatabaseModel())
        }
    }

    //delete old asteroid data
    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deletePreviousAsteroids(getYesterday(), getPastDate())
        }
    }

    //refresh POD
    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            val pod = asteroidApiService.getPictureOfDay(API_KEY)
            database.podDao.insertPOD(pod.asDatabaseModel())
        }
    }
}



