package com.crp.wikiAppNew

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.crp.wikiAppNew.dao.PlaceDao
import com.crp.wikiAppNew.database.AppDatabase
import com.crp.wikiAppNew.datamodel.Page
import com.crp.wikiAppNew.datamodel.PlaceEntity
import com.crp.wikiAppNew.network.WikiAPI
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class LocationService : Service() {

    private val TAG = "LocationService"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private var notificationCounter = 2
    private val wikiAPI: WikiAPI by inject()
    private var currentPlaceName: String? = null
    private var lastNotifiedPlaceName: String? = null
    private lateinit var placesClient: PlacesClient

    override fun onCreate() {
        super.onCreate()
        initializeLocationServices()
        initializePlacesClient()
        setupNotificationManager()
        createNotificationChannelIfNeeded()
        startServiceWithNotification("Service running")
        requestLocationUpdates()
    }

    private fun initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initializePlacesClient() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GOOGLE_API_KEY)
        }
        placesClient = Places.createClient(this)
    }

    private fun setupNotificationManager() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "locationServiceChannel",
                "Location Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Location Service"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startServiceWithNotification(message: String) {
        startForeground(1, buildServiceNotification(message))
    }

    private fun buildServiceNotification(message: String): Notification {
        return NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Location Service")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 30000L
        ).apply {
            setMinUpdateIntervalMillis(10000L)
        }.build()

        if (hasLocationPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                fetchPlaceName(location.latitude, location.longitude)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchPlaceName(latitude: Double, longitude: Double) {
        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.TYPES)
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        placesClient.findCurrentPlace(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                handlePlaceResult(task.result, latitude, longitude)
            } else {
                handlePlaceError(task.exception)
            }
        }
    }

    private fun handlePlaceResult(response: FindCurrentPlaceResponse, latitude: Double, longitude: Double) {
        val topPlace = response.placeLikelihoods.maxByOrNull { it.likelihood }
        topPlace?.let {
            val placeName = it.place.name
            if (placeName != currentPlaceName) {
                currentPlaceName = placeName
                fetchWikiDataAndUpdateDatabase(placeName, latitude, longitude)
            }
        }
    }

    private fun handlePlaceError(exception: Exception?) {
        if (exception is ApiException) {
            Log.e(TAG, "Place not found: ${exception.statusCode}")
        }
    }

    private fun fetchWikiDataAndUpdateDatabase(placeName: String, latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val wikiResponse = wikiAPI.getSearchRespone(
                placeName, "query", "2", "extracts|pageimages|pageterms",
                "2", "true", "thumbnail", "json", "prefixsearch", "300", "true"
            )

            withContext(Dispatchers.Main) {
                if (wikiResponse.query?.pages?.isNotEmpty() == true) {
                    processWikiResponse(wikiResponse.query.pages[0], placeName, latitude, longitude)
                } else {
                    Log.d(TAG, "No results found for $placeName")
                }
            }
        }
    }

    private fun processWikiResponse(page: Page?, placeName: String, latitude: Double, longitude: Double) {
        page?.let {
            val articleTitle = it.title
            val articleExtract = it.extract
            val articleThumbnailUrl = it.thumbnail?.source

            if (articleTitle != null) {
                val placeEntity = createPlaceEntity(latitude, longitude, placeName, articleTitle, articleExtract, articleThumbnailUrl)
                checkAndUpdatePlaceVisitCount(placeEntity)
            }
        }
    }

    private fun createPlaceEntity(
        latitude: Double, longitude: Double, placeName: String,
        articleTitle: String, articleExtract: String?, articleThumbnailUrl: String?
    ): PlaceEntity {
        return PlaceEntity(
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis(),
            wikipediaTitle = articleTitle,
            wikiDescription = articleExtract ?: "",
            placeName = placeName,
            thumbnailUrl = articleThumbnailUrl ?: ""
        )
    }

    private fun savePlaceToDatabase(placeEntity: PlaceEntity) {
        val db = AppDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db.PlaceDao().insert(placeEntity)
        }
    }

    private fun logSavedPlaces() {
        val db = AppDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val places = db.PlaceDao().getAll()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Saved places: $places")
            }
        }
    }

    private fun displayNotification(placeName: String, articleTitle: String, placeEntity: PlaceEntity) {
        notificationCounter++

        val coordinates = LatLng(placeEntity.latitude, placeEntity.longitude)
        val formattedCoordinates = String.format("%.6f, %.6f", coordinates.latitude, coordinates.longitude)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://en.wikipedia.org/wiki/$articleTitle")
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Article Found")
            .setContentText("Article: $articleTitle\nPlace: $placeName\nCoordinates: $formattedCoordinates")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .addAction(NotificationCompat.Action(android.R.drawable.ic_menu_view, "View", pendingIntent))
            .build()

        if (placeName != lastNotifiedPlaceName) {
            notificationManager.notify(notificationCounter, notification)
            lastNotifiedPlaceName = placeName
        }
    }

    private fun checkAndUpdatePlaceVisitCount(newPlaceEntity: PlaceEntity) {
        val db = AppDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val existingPlace = db.PlaceDao().findByName(newPlaceEntity.placeName)
            if (existingPlace != null) {
                val updatedPlace = existingPlace.copy(visitCount = existingPlace.visitCount + 1)
                db.PlaceDao().update(updatedPlace)
            } else {
                savePlaceToDatabase(newPlaceEntity)
            }
            withContext(Dispatchers.Main) {
                logSavedPlaces()
                displayNotification(newPlaceEntity.placeName, newPlaceEntity.wikipediaTitle, newPlaceEntity)
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
