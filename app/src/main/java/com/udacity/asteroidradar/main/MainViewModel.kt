package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.data.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel (application: Application): ViewModel() {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    val asteroids = asteroidsRepository.asteroids
    val pictureOfDay = asteroidsRepository.pictureOfDay

    private val _selectedOption = MutableLiveData<MainFragment.SelectedOption>()
    val selectedOption: LiveData<MainFragment.SelectedOption>
        get() = _selectedOption

    private val _navigateToDetails = MutableLiveData<Asteroid>()
    val navigateToDetails: LiveData<Asteroid>
        get() = _navigateToDetails
    init {
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshPictureOfDay()
                showSelectedOption(MainFragment.SelectedOption.TODAY)
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {

            }
        }
    }
    val asteroidList = Transformations.switchMap(_selectedOption){
        when (it) {
            MainFragment.SelectedOption.TODAY -> asteroidsRepository.asteroids
            MainFragment.SelectedOption.SAVED -> asteroidsRepository.asteroidSaved
            MainFragment.SelectedOption.WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidSaved
        }
    }

    fun showSelectedOption(selectedOption: MainFragment.SelectedOption) {
        _selectedOption.value = selectedOption
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    fun displayAsteroidsDone() {
        _navigateToDetails.value = null
    }
    @Suppress("UNCHECKED_CAST")
    class Factory(val app: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }

}