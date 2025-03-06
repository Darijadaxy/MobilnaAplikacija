package com.example.restorani.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.restorani.MainActivity
import com.example.restorani.R
import com.example.restorani.data.repositories.AuthRepo
import com.example.restorani.data.repositories.Resource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt



class LocationService: Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private val restaurantsWithoutDuplicates = mutableSetOf<String>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        createNotificationChannel()
        locationClient = LocationClientImpl(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "Service started with action: ${intent?.action}")

        when(intent?.action){
            ACTION_START -> {
                Log.d("LocationService", "Service started")
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
                start()
            }
            ACTION_STOP -> {
                Log.d("LocationService", "Service stopped")
                stop()
            }
            ACTION_FIND_NEARBY -> {
                Log.d("NearbyService", "Service started")
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
                start(restaurantIsNearby = true)

            }
        }
        return START_NOT_STICKY
    }

    private fun start(

        restaurantIsNearby: Boolean = false
    ) {
        locationClient.getLocationUpdates(5000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                sharedPreferences.edit()
                    .putString("last_latitude", location.latitude.toString())
                    .putString("last_longitude", location.longitude.toString())
                    .apply()

                val intent = Intent(ACTION_LOCATION_UPDATE).apply {
                    putExtra(EXTRA_LOCATION_LATITUDE, location.latitude)
                    putExtra(EXTRA_LOCATION_LONGITUDE, location.longitude)
                }
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                if(restaurantIsNearby){
                    checkProximityToRestaurants(location.latitude, location.longitude)
                }

            }.launchIn(serviceScope)
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {

        val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
        val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()

        if (lastLatitude != null && lastLongitude != null) {
            val lastLocation = LatLng(lastLatitude, lastLongitude)
        }

        super.onDestroy()
        Log.d("LocationService", "Service stopped")
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        val notificationChannelId = "LOCATION_SERVICE_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Lokacija",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Obaveštavamo vas da se vaša lokacija prati u pozadini kako bi se proverilo da li ste u blizini nekog restorana"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): android.app.Notification {
        val notificationChannelId = "LOCATION_SERVICE_CHANNEL"

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Praćenje lokacije")
            .setContentText("Vaša lokacija se trenutno prati")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 //radius zemlje
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R*c;
        return distance
    }


    private fun checkProximityToRestaurants(userLatitude: Double, userLongitude: Double) {
        val firestore = FirebaseFirestore.getInstance()
        val authRepository = AuthRepo()

        serviceScope.launch {
            val userResource = authRepository.getUser()

            if (userResource is Resource.Success) {


                firestore.collection("restaurants").get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val geoPoint = document.getGeoPoint("location")
                            //val restaurantUserId = document.getString("userId")

                            if (geoPoint != null ) {
                                val distance = calculateHaversineDistance(userLatitude, userLongitude, geoPoint.latitude, geoPoint.longitude)

                                if (distance <= 500 && !restaurantsWithoutDuplicates.contains(document.id)) {
                                    restaurantNearby(document.getString("name") ?: "Restaurant")
                                    restaurantsWithoutDuplicates.add(document.id)
                                    Log.d("NearbyRestaurant", document.toString())
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("LocationService", "Error fetching restaurants", e)
                    }
            } else {
                Log.e("LocationService", "Failed to fetch current user: ${(userResource as Resource.Failure).exception.message}")
            }
        }
    }

    private fun restaurantNearby(restaurantName: String) {
        val notificationChannelId = "LOCATION_SERVICE_CHANNEL"

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Restoran je u blizini!")
            .setContentText("Na manje od 500m je restoran \"$restaurantName\"!")
            .setSmallIcon(R.drawable.restoran)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NEARBY_RESTAURANT_NOTIFICATION_ID, notification)
    }



    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_FIND_NEARBY = "ACTION_FIND_NEARBY"
        const val ACTION_LOCATION_UPDATE = "ACTION_LOCATION_UPDATE"
        const val EXTRA_LOCATION_LATITUDE = "EXTRA_LOCATION_LATITUDE"
        const val EXTRA_LOCATION_LONGITUDE = "EXTRA_LOCATION_LONGITUDE"
        private const val NOTIFICATION_ID = 1
        private const val NEARBY_RESTAURANT_NOTIFICATION_ID = 25
    }
}

