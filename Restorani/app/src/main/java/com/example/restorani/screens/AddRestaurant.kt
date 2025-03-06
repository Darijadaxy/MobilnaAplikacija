package com.example.restorani.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restorani.data.repositories.Resource
import com.example.restorani.screens.components.ColorPalette
import com.example.restorani.screens.components.DashedLineBackground
import com.example.restorani.screens.components.Header
import com.example.restorani.screens.components.InputFieldLabel
import com.example.restorani.screens.components.RestaurantDataInput
import com.example.restorani.screens.components.Secondary
import com.example.restorani.screens.components.SignUpInButton
import com.example.restorani.screens.components.UploadRestaurantImages
import com.example.restorani.view_models.RestaurantViewModel
import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp


@Composable
fun AddRestaurantScreen(
    restaurantViewModel: RestaurantViewModel,
    navController: NavController,
    location: MutableState<LatLng?>,
    onDismiss: () -> Unit

) {

    val restaurantFlow = restaurantViewModel.restaurantFlow.collectAsState()

    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }

    val selectedMoreImages = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    DashedLineBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Header(header_text = "Dodaj novi restoran!")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Secondary(secondary_text = "Obogatite svoju kolekciju restorana i podelite iskustva sa zajednicom!")

                Spacer(modifier = Modifier.height(8.dp))

                InputFieldLabel(label = "Ime")
                Spacer(modifier = Modifier.height(2.dp))
                RestaurantDataInput(
                    hint = "Unesite ime restorana",
                    value = name
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputFieldLabel(label = "Opis")
                Spacer(modifier = Modifier.height(2.dp))
                RestaurantDataInput(
                    hint = "Unesite opis restorana",
                    value = description
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBCAAA4),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))
                        InputFieldLabel(label = "Izaberite slike restorana.")
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadRestaurantImages(selectedMoreImages)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SignUpInButton(
                    onClick = {
                        restaurantViewModel.saveRestaurant(
                            location = location.value!!,
                            name = name.value,
                            description = description.value,
                            restaurantImages = selectedMoreImages.value,
                            date = Timestamp.now()
                        )
                        onDismiss()
                        navController.popBackStack("mapScreen", inclusive = false)

                    },
                    text = "Dodaj restoran",
                    textColor = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, end = 20.dp,start=20.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorPalette.Purple200,
                        contentColor = Color.White
                    )
                ) {
                    Text("X")
                }
            }
        }
    }
}

