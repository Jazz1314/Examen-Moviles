package com.crp.wikiAppNew.viewmodel

import androidx.lifecycle.LiveData

import com.crp.wikiAppNew.datamodel.PlaceEntity
import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import com.crp.wikiAppNew.SettingsFragment
import com.crp.wikiAppNew.dao.PlaceDao
import com.crp.wikiAppNew.database.AppDatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesView(application: Application) : AndroidViewModel(application) {
    private val placeDao: PlaceDao = AppDatabase.getInstance(application).PlaceDao()
    val places: LiveData<List<PlaceEntity>> = placeDao.getAll()

    fun getTopVisitedPlaces(context: Context, callback: (List<PlaceEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val topVisitedThreshold = sharedPreferences.getInt(SettingsFragment.KEY_FREQUENT_PLACES_COUNT, SettingsFragment.DEFAULT_FREQUENT_PLACES_COUNT)

            val topVisitedList = placeDao.getTopVisitedPlaces(topVisitedThreshold)
            withContext(Dispatchers.Main) {
                topVisitedList.observeForever { places ->
                    callback(places)
                }
            }
        }
    }

    fun addPlace(place: PlaceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            placeDao.insert(place)
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            placeDao.delete(place)
        }
    }

    fun updatePlace(oldPlace: PlaceEntity, newPlace: PlaceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            placeDao.update(newPlace)
        }
    }
}
