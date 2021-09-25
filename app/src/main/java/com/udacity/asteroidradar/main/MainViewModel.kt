package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.data.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel (application: Application): ViewModel() {

    //define db variable
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    //all asteroids and POD available to application
    val asteroids = asteroidsRepository.asteroids
    val pictureOfDay = asteroidsRepository.pictureOfDay

    //handle asteroid filter
    private val _selectedOption = MutableLiveData<MainFragment.SelectedOption>()
    val selectedOption: LiveData<MainFragment.SelectedOption>
        get() = _selectedOption

    //navigation variable
    private val _navigateToDetails = MutableLiveData<Asteroid>()
    val navigateToDetails: LiveData<Asteroid>
        get() = _navigateToDetails


    init {
        //refresh list from repo
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshPictureOfDay()
                showSelectedOption(MainFragment.SelectedOption.TODAY)
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {

            }
        }
    }

    //load list of asteroids based on overflow menu filters
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

    //set navigation variable when asteroid item is clicked
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    //done navigating to details
    fun displayAsteroidsDone() {
        _navigateToDetails.value = null
    }

    /*
    * Factory for constructing MainViewModel with param
    */
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