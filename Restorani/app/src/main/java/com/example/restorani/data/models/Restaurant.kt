package com.example.restorani.data.models
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Restaurant (
    @DocumentId val id: String = "",
    val userId: String = "",

    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val name: String = "",
    val description: String = "",
    val restaurantImages: List<String> = emptyList(),
    val comments: List<String> = emptyList(),
    val ratings: List<Int> = emptyList(), // Lista ocena
    val averageRating: Double = 0.0 ,// Prosečna ocena
    val date: Timestamp?=null

)

