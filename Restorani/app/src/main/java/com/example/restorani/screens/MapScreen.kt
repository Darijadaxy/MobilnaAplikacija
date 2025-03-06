
package com.example.restorani.screens

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.restorani.view_models.AuthVM
import com.example.restorani.view_models.RestaurantViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


import com.example.restorani.data.models.Restaurant
import com.example.restorani.data.repositories.Resource
import com.example.restorani.location.LocationService


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalDrawer
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.restorani.R
import com.example.restorani.data.models.User
import com.example.restorani.screens.components.ColorPalette
import com.example.restorani.screens.components.DashedLineBackground
import com.example.restorani.screens.components.Header
import com.example.restorani.screens.components.InputFieldLabel
import com.example.restorani.screens.components.bitmapDescriptorFromVector2
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Filter (

    val fullName: String? = null,
    val name: String? = null,
    val averageRating: Double? = null,
    val dateRange: Pair<Timestamp?, Timestamp?> = Pair(null, null),
    val radius: Double? = null

)
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadius * c
}


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    navController: NavController,
    //authVM: AuthVM,
    restaurantViewModel: RestaurantViewModel,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    myLocation: MutableState<LatLng?> = remember { mutableStateOf(null) }
) {
    val context = LocalContext.current

    val showSheet = remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", true)
    val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
    val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()


    val restaurantCollection = restaurantViewModel.restaurants.collectAsState()
    val restaurantsList = remember { mutableListOf<Restaurant>() }

    val userCollection = restaurantViewModel.users.collectAsState()
    val usersList = remember { mutableListOf<User>() }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf<Filter?>(null) }


    LaunchedEffect(restaurantCollection.value) {
        when (val result = restaurantCollection.value) {
            is Resource.Success -> {
                restaurantsList.clear()
                restaurantsList.addAll(result.result)
            }
            is Resource.Failure -> {
                Log.e("MapScreen", "Failed to load restaurants:", result.exception)
            }
            is Resource.Loading -> {
                Log.d("MapScreen", "Loading restaurants...")
            }
            null -> {
                Log.d("MapScreen", "No restaurants available")
            }
        }
    }
    userCollection.value.let {
        when (it) {
            is Resource.Success -> {
                usersList.clear()
                usersList.addAll(it.result)
                Log.d("MapScreen", "Loaded ${usersList.size} users")
            }

            is Resource.Loading -> {
                Log.d("MapScreen", "Loading users...")
            }

            is Resource.Failure -> {
                Log.e("MapScreen", "Failed to load users: ${it.exception.message}")
            }

            null -> {
                Log.d("MapScreen", "No users available")
            }
        }
    }

    Log.d("MapScreen", "MapScreen Composable Started")


    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("MapScreen", "Permissions not granted, requesting permissions")
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    } else {
        // Start LocationService
        Log.d("MapScreen", "Permissions granted, starting LocationService")

        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startForegroundService(this)
        }
    }

    if (!isTrackingServiceEnabled && lastLatitude != null && lastLongitude != null) {
        val lastLocation = LatLng(lastLatitude, lastLongitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(lastLocation, 17f)
    }


    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) {
                    val latitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LATITUDE, 0.0)
                    val longitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LONGITUDE, 0.0)
                    myLocation.value = LatLng(latitude, longitude)
                }
            }
        }
    }


    DisposableEffect(context) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(LocationService.ACTION_LOCATION_UPDATE))
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(myLocation.value) {
        myLocation.value?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 17f)
        }
    }


    LaunchedEffect(showSheet.value) {
        if (showSheet.value) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            AddRestaurantScreen(
                restaurantViewModel = restaurantViewModel,
                navController = navController,
                location = myLocation,
                onDismiss = { showSheet.value = false }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Mapa Restorana",
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {

                            navController.navigate("tableScreen")
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {

                            showFilterDialog = true
                        }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filteri",tint = Color.White)
                        }
                    },
                    backgroundColor = ColorPalette.Purple200,
                    contentColor = Color.White
                )


            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    myLocation.value?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Tvoja lokacija",
                            snippet = "Trenutna lokacija korisnika",
                            onClick = {
                                showSheet.value = true
                                true
                            }
                        )
                    }

                    if (filter == null) {
                        restaurantsList.forEach { restaurant ->
                            Log.d(
                                "MapScreen",
                                "Restaurant: ${restaurant.name}, Location: ${restaurant.location.latitude}, ${restaurant.location.longitude}"
                            )
                            val restaurantLocation =
                                LatLng(restaurant.location.latitude, restaurant.location.longitude)

                            Marker(
                                state = MarkerState(position = restaurantLocation),
                                title = restaurant.name,
                                icon = bitmapDescriptorFromVector2(context, R.drawable.restoran),
                                onClick = {
                                    Log.d("MapScreen", "Kliknuto na restoran: ${restaurant.name}")
                                    navController.navigate("restaurantDet/${restaurant.id}")
                                    true
                                }
                            )
                        }
                    } else {
                        restaurantsList.filter { restaurant ->
                            val user = usersList.find { it.id == restaurant.userId }


                            val matchesFullName = filter?.fullName?.let {
                                user?.fullName?.contains(it, ignoreCase = true)
                            } ?: true

                            val matchesName = filter?.name?.let {
                                restaurant.name.contains(it, ignoreCase = true)
                            } ?: true

                            val matchesRating = filter?.averageRating?.let {
                                restaurant.averageRating >= it
                            } ?: true

                            val matchesDate = filter?.dateRange?.let { (startDate, endDate) ->
                                val restaurantDate = restaurant.date

                                val matchesStartDate = startDate?.let {
                                    restaurantDate!! >= it
                                } ?: true

                                val matchesEndDate = endDate?.let {
                                    restaurantDate!! <= it
                                } ?: true

                                matchesStartDate && matchesEndDate
                            } ?: true

                            val matchesRadius = filter?.radius?.let { radius ->
                                myLocation.value?.let { currentLocation ->
                                    val distance = calculateDistance(
                                        currentLocation.latitude,
                                        currentLocation.longitude,
                                        restaurant.location.latitude,
                                        restaurant.location.longitude
                                    )
                                    distance <= radius
                                }
                            } ?: true

                            matchesFullName && matchesName && matchesRating && matchesDate && matchesRadius
                        }.forEach { filteredRestaurant ->
                            val restaurantLocation = LatLng(
                                filteredRestaurant.location.latitude,
                                filteredRestaurant.location.longitude
                            )
                            Marker(
                                state = MarkerState(position = restaurantLocation),
                                title = filteredRestaurant.name,
                                icon = bitmapDescriptorFromVector2(context, R.drawable.restoran),
                                onClick = {
                                    Log.d(
                                        "MapScreen",
                                        "Kliknuto na restoran: ${filteredRestaurant.name}"
                                    )
                                    navController.navigate("restaurantDet/${filteredRestaurant.id}")
                                    true
                                }
                            )
                        }
                    }
                }
            }
            if (showFilterDialog) {
                FilterDialog(
                    onDismiss = { showFilterDialog = false },
                    onApplyFilter = { criteria ->
                        filter = criteria
                        showFilterDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilter: (Filter) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var averageRating by remember { mutableStateOf <Double?> (null)}
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var radius by remember { mutableStateOf<Double?>(null) }


    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = ColorPalette.BackgroundMain,

        title = {
            Text(
                text = "Filteri",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
                style = MaterialTheme.typography.h4.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorPalette.Purple200
            )
        },


        text = {
            Column {


                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(text="Unesite puno ime vlasnika",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp
                        )
                       ) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White,
                        backgroundColor = Color.Transparent, // Postavljanje pozadinske boje na providnu
                        focusedLabelColor = ColorPalette.Purple200, // Boja labela kada je polje fokusirano
                        unfocusedLabelColor = Color.White, // Boja labela kada polje nije fokusirano
                        focusedIndicatorColor = ColorPalette.Purple200, // Boja linije kada je polje fokusirano
                        unfocusedIndicatorColor = Color.White // Boja linije kada polje nije fokusirano
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))


                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text="Naziv restorana",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp
                        )) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White,
                        backgroundColor = Color.Transparent,
                        focusedLabelColor = ColorPalette.Purple200,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = ColorPalette.Purple200,
                        unfocusedIndicatorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))


                TextField(
                    value = averageRating?.toString() ?: "",
                    onValueChange = {
                        averageRating = it.toDoubleOrNull()
                    },
                    label = { Text(text="Minimalna prosečna ocena",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp
                        )) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White,
                        backgroundColor = Color.Transparent,
                        focusedLabelColor = ColorPalette.Purple200,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = ColorPalette.Purple200,
                        unfocusedIndicatorColor = Color.White
                    )

                )


                Spacer(modifier = Modifier.height(8.dp))

                // Add Date Picker for start date
                DatePicker(
                    label = "Početni datum",
                    selectedDate = startDate,
                    onDateChange = { startDate = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                DatePicker(
                    label = "Krajnji datum",
                    selectedDate = endDate,
                    onDateChange = { endDate = it }
                )

                Spacer(modifier = Modifier.height(8.dp))


                TextField(
                    value = radius?.toString() ?: "",
                    onValueChange = { radius = it.toDoubleOrNull() },
                    label = { Text(text="Radijus (km)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp
                        )) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White,
                        backgroundColor = Color.Transparent,
                        focusedLabelColor = ColorPalette.Purple200,
                        unfocusedLabelColor = Color.White,
                        focusedIndicatorColor = ColorPalette.Purple200,
                        unfocusedIndicatorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        },
        confirmButton = {

            Button(
                onClick = {
                    onApplyFilter(
                        Filter(
                            fullName = fullName,
                            name = name,
                            averageRating=averageRating,
                            dateRange = Pair(
                                startDate?.let { Timestamp(it) },
                                endDate?.let { Timestamp(it) }
                            ) ,
                            radius = radius
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ColorPalette.Purple200,
                    contentColor = Color.White
            )) {
                Text("Filtriraj")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ColorPalette.Purple200,
                    contentColor = Color.White
            )) {
                Text("Odustani")
            }
        }
    )
}


@Composable
fun DatePicker(
    label: String,
    selectedDate: Date?,
    onDateChange: (Date?) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            R.style.CustomDatePickerDialog,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateChange(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedButton(onClick = { datePickerDialog.show() },
         modifier = Modifier.width(150.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = ColorPalette.Purple200,
        contentColor = Color.White
    )) {
        Text(text = if (selectedDate != null) dateFormat.format(selectedDate) else label)
    }
}

