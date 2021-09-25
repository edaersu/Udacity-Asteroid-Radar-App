package com.udacity.asteroidradar.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

@Entity
data class DatabaseAsteroid constructor (@PrimaryKey
                                         val id: Long,
                                         val codename: String, val closeApproachDate: String,
                                         val absoluteMagnitude: Double, val estimatedDiameter: Double,
                                         val relativeVelocity: Double, val distanceFromEarth: Double,
                                         val isPotentiallyHazardous: Boolean)

//extension func. that converts db object to domain object
fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

/* Db object for picture of the day*/

@Entity
data class DatabasePictureOfDay(
    @PrimaryKey
    val url: String,
    val mediaType: String,
    val title: String
)

//convert db object to domain object
fun DatabasePictureOfDay.asDomainModelPicture(): PictureOfDay {
    return PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}