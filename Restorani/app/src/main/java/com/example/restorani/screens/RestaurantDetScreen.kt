package com.example.restorani.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.restorani.R
import com.example.restorani.data.models.Restaurant
//import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


import androidx.compose.ui.draw.clip // Import for clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.restorani.screens.components.ColorPalette
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.restorani.screens.components.DashedLineBackground
import com.example.restorani.screens.components.Header
import com.example.restorani.screens.components.InputFieldLabel

@Composable
fun RestaurantDetScreen(
    restaurant: Restaurant,
    onBack: () -> Unit,
    onAddComment: (String, String) -> Unit,
    onAddRating: (Int, String) -> Unit,
    uid: String
) {

    val commentText = remember { mutableStateOf("") }
    val ratingText = remember { mutableStateOf("") }
    val comments = remember { mutableStateOf(restaurant.comments) }
    val averageRating = remember { mutableStateOf(restaurant.averageRating) }

    DashedLineBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Header(header_text = restaurant.name)

            Text(
                text = "Opis:",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            InputFieldLabel(label = restaurant.description)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Slike:",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(modifier = Modifier.padding(bottom = 16.dp)) {
                items(restaurant.restaurantImages) { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Slika restorana",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }


            Text(
                text = "Prosečna ocena: ${averageRating.value}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )


            Row(modifier = Modifier.padding(top = 16.dp)) {
                TextField(
                    value = ratingText.value,
                    onValueChange = { ratingText.value = it },
                    placeholder = {
                        Text(
                            text = "Dodaj ocenu (1-5)...",
                            style = TextStyle(fontSize = 14.sp, color = Color.White)
                        )
                    },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Button(
                    onClick = {
                        val rating = ratingText.value.toIntOrNull()
                        if (rating != null && rating in 1..5) {
                            onAddRating(rating, uid)
                            ratingText.value = ""

                            val newAverageRating = (averageRating.value * restaurant.ratings.size + rating) / (restaurant.ratings.size + 1)
                            averageRating.value = newAverageRating
                            //  averageRating.value =String.format("%.2f", newAverageRating).toDouble()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorPalette.Purple200,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Dodaj ocenu")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Komentari:",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )


            LazyColumn(modifier = Modifier.weight(1f)) {
                items(comments.value) { comment ->
                    Text(
                        text = comment,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    )
                }
            }


            Row(modifier = Modifier.padding(top = 16.dp)) {
                TextField(
                    value = commentText.value,
                    onValueChange = { commentText.value = it },
                    placeholder = {
                        Text(
                            text = "Dodaj komentar...",
                            style = TextStyle(fontSize = 14.sp, color = Color.White)
                        )
                    },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Button(
                    onClick = {
                        onAddComment(commentText.value, uid)
                        comments.value = comments.value + commentText.value
                        commentText.value = ""
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorPalette.Purple200,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Dodaj")
                }
            }
        }
    }

}

