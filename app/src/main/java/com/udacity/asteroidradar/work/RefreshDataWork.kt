package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository

class RefreshDataWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        //db instance
        val database = getDatabase(applicationContext)
        //repo instance
        val repository = AsteroidRepository(database)

        //check if work executes successfully
        return try {
            repository.refreshAsteroids()
            repository.deleteOldAsteroids()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}