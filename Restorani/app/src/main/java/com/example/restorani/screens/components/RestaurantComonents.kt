package com.example.restorani.screens.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.restorani.R
import com.example.restorani.data.models.Restaurant



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDataInput(
    onValueChange: (String) -> Unit = {},
    hint: String,
    value: MutableState<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        TextField(
            value = value.value,
            onValueChange = { newValue ->
                value.value = newValue
                onValueChange(newValue)
            },
            singleLine = true,
            placeholder = {
                Text(
                    text = hint,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                containerColor = ColorPalette.BackgroundMain

            ),

            keyboardOptions = KeyboardOptions.Default,
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = Color.White
            )
        )
    }
}

@Composable
fun UploadRestaurantImages(
    selectedImages: MutableState<List<Uri>>
) {
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null) {
            selectedImages.value += uris
        }
    }

    LazyRow {
        if (selectedImages.value.size < 5) {
            item {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(100.dp)
                        .height(100.dp)
                        .border(
                            1.dp,
                            color = Color(0xFFC1C1C1),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .background(
                            color = Color(0xFFC1C1C1),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable { pickImagesLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = "")
                }
            }
        }
        items(selectedImages.value.size) { index ->
            val uri = selectedImages.value[index]
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        1.dp,
                        Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clickable { selectedImages.value -= uri },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}
