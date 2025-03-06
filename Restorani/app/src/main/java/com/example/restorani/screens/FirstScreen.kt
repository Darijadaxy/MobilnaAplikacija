package com.example.restorani.screens

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.Logout
//import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restorani.view_models.AuthVM
import com.example.restorani.app_navigation.Routes
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.restorani.R
import com.example.restorani.location.LocationService
import com.example.restorani.screens.components.ColorPalette


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FirstScreen(
    navController: NavController,
    authVM: AuthVM
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", false)

    val checked = remember {
        mutableStateOf(isTrackingServiceEnabled)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.BackgroundMain),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.rpocetna),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )


            Text(
                text = "Dobrodošli u Restaurant Review App!",
                color = ColorPalette.Purple200,
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            Text(
                text = "Ocenite i komentarišite restorane. Pronađite najbolja mesta za jelo u vašoj blizini! \n\nDelite svoja iskustva sa zajednicom i pomozite drugima da pronađu savršeno mesto za obrok.",
                color = Color.White,
                style = TextStyle(fontSize = 16.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )


            Button(
                onClick = {
                    navController.navigate(Routes.mapScreen)
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(280.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPalette.Purple200,
                    contentColor = ColorPalette.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "Idi na mapu", color = ColorPalette.White)
            }


            Button(
                onClick = {
                    navController.navigate(Routes.rangScreen)
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(280.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPalette.Purple200,
                    contentColor = ColorPalette.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "Rang lista", color = ColorPalette.White)
            }


            Button(
                onClick = {
                    authVM.signOut()
                    navController.navigate(Routes.signInScreen)
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(280.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPalette.Purple200,
                    contentColor = ColorPalette.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "Odjavi se", color = ColorPalette.White)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Obaveštenja za restorane u blizini  ",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
                Switch(
                    checked = checked.value,
                    onCheckedChange = {
                        checked.value = it
                        if (it) {
                            Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_FIND_NEARBY
                                context.startForegroundService(this)
                            }
                            with(sharedPreferences.edit()) {
                                putBoolean("tracking_location", true)
                                apply()
                            }
                        } else {
                            Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_STOP
                                context.stopService(this)
                                Log.d("ServiceSettings", "Stop action sent")
                            }
                            Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_START
                                context.startForegroundService(this)
                                Log.d("ServiceSettings", "Start action sent")
                            }
                            with(sharedPreferences.edit()) {
                                putBoolean("tracking_location", false)
                                apply()
                            }
                        }
                    },
                    thumbContent = if (checked.value) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

