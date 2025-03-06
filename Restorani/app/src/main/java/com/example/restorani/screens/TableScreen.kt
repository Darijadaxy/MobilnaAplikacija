package com.example.restorani.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.restorani.data.models.Restaurant
import com.example.restorani.data.repositories.Resource
import com.example.restorani.screens.components.ColorPalette
import com.example.restorani.view_models.RestaurantViewModel


@Composable
fun TableScreen(
    restaurantViewModel: RestaurantViewModel
) {
    val restaurantCollection by restaurantViewModel.restaurants.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.BackgroundMain) // Postavljanje pozadine celog ekrana
            .padding(8.dp) // Dodajte padding ako želite
    ) {

        when (restaurantCollection) {
            is Resource.Success -> {
                val restaurants =
                    (restaurantCollection as Resource.Success<List<Restaurant>>).result
                LazyColumn {
                    item {
                        TableHeader()
                    }
                    items(restaurants) { restaurant ->
                        TableRow(
                            name = restaurant.name,
                            description = restaurant.description,
                            averageRating = restaurant.averageRating
                        )
                    }
                }
            }

            is Resource.Loading -> {
                CircularProgressIndicator()
            }

            is Resource.Failure -> {
                val error = (restaurantCollection as Resource.Failure).exception
                Text("Failed to load restaurants: ${error.message}")
            }

            null -> {
                Text("No restaurants available")
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorPalette.Purple200)
            .border(1.dp, Color.White)
            .padding(4.dp)
    ) {
        TableCell("Ime", Modifier.weight(1f),ColorPalette.Purple200, Color.White)
        TableCell("Opis", Modifier.weight(2f),ColorPalette.Purple200, Color.White)
        TableCell("Prosečna ocena", Modifier.weight(1f),ColorPalette.Purple200, Color.White)
    }
}

@Composable
fun TableRow(
    name: String,
    description: String,
    averageRating: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF393E46))
            .border(1.dp, Color.White)
            .padding(4.dp)
    ) {
        TableCell(name, Modifier.weight(1f), Color(0xFF393E46), Color.White)
        TableCell(description, Modifier.weight(2f), Color(0xFF393E46), Color.White)
        TableCell(String.format("%.1f", averageRating), Modifier.weight(1f), Color(0xFF393E46), Color.White)
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp
        )
    }
}
