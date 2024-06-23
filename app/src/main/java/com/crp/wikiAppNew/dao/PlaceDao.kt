package com.crp.wikiAppNew.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.crp.wikiAppNew.datamodel.PlaceEntity
@Dao
interface PlaceDao {


    @Query("SELECT * FROM PlaceEntity")
    fun getAll(): LiveData<List<PlaceEntity>>

    @Insert
    fun insert(place: PlaceEntity)

    @Delete
    fun delete(entity: PlaceEntity)
    @Update
    fun update(entity: PlaceEntity)

    @Query("SELECT * FROM PlaceEntity WHERE latitude = :latitude AND longitude = :longitude")
    fun getPlaceByCoordinates(latitude: Double, longitude: Double): PlaceEntity?

    @Query("SELECT * FROM PlaceEntity WHERE placeName = :name")
    fun findByName(name: String): PlaceEntity?

    @Query("SELECT * FROM PlaceEntity WHERE visitCount > :threshold ORDER BY visitCount DESC")
    fun getTopVisitedPlaces(threshold: Int): LiveData<List<PlaceEntity>>
}