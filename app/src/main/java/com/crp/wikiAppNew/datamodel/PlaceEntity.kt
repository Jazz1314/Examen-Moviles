package com.crp.wikiAppNew.datamodel
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val wikipediaTitle: String,
    val wikiDescription: String,
    val thumbnailUrl: String,
    val placeName: String,
    var visitCount: Int = 0
)