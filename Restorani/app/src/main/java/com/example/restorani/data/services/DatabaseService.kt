package com.example.restorani.data.services

import com.example.restorani.data.models.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.restorani.data.models.User
import com.example.restorani.data.repositories.Resource


class DatabaseService( private val firestore: FirebaseFirestore) {

    suspend fun saveUserData( userId: String, user: User ) : Resource<String>
    {
        return try
        {
            firestore.collection("users").document(userId).set(user).await()
            Resource.Success("[INFO] User data saved successfully. (User ID: ${userId})")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    suspend fun saveRestaurant(
        restaurant: Restaurant
    ): Resource<String>{
        return try{
            firestore.collection("restaurants").add(restaurant).await()
            Resource.Success("Podaci o restoranu su uspesno sacuvani")
        }catch(e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
    suspend fun addCommentToRestaurant(restaurantId: String, comment: String): Resource<String> {
        return try {
            val restaurantRef = firestore.collection("restaurants").document(restaurantId)
            val restaurantSnapshot = restaurantRef.get().await()

            if (restaurantSnapshot.exists()) {
                val restaurant = restaurantSnapshot.toObject(Restaurant::class.java)
                if (restaurant != null) {
                    val updatedComments = restaurant.comments.toMutableList()
                    updatedComments.add(comment)
                    restaurantRef.update("comments", updatedComments).await()
                    Resource.Success("Komentar uspešno dodat")
                } else {
                    Resource.Failure(Exception("[ERROR] Restaurant object is null"))
                }
            } else {
                Resource.Failure(Exception("[ERROR] Restaurant document not found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
    suspend fun addRatingToRestaurant(restaurantId: String, rating: Int): Resource<String> {
        return try {
            val restaurantRef = firestore.collection("restaurants").document(restaurantId)
            val restaurantSnapshot = restaurantRef.get().await()

            if (restaurantSnapshot.exists()) {
                val restaurant = restaurantSnapshot.toObject(Restaurant::class.java)
                if (restaurant != null) {
                    val updatedRatings = restaurant.ratings.toMutableList()
                    updatedRatings.add(rating)
                    val averageRating = updatedRatings.average()

                    restaurantRef.update("ratings", updatedRatings, "averageRating", averageRating).await()
                    Resource.Success("Ocena uspešno dodata")
                } else {
                    Resource.Failure(Exception("[ERROR] Restaurant object is null"))
                }
            } else {
                Resource.Failure(Exception("[ERROR] Restaurant document not found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun updateUserPoints(
        uid: String,
        points: Int
    ): Resource<String>{
        return try {
            val userDocRef = firestore.collection("users").document(uid)
            val userSnapshot = userDocRef.get().await()

            if(userSnapshot.exists()){
                val user = userSnapshot.toObject(User::class.java)
                if(user != null){
                    val newPoints = user.points + points
                    userDocRef.update("points", newPoints).await()
                    Resource.Success("Uspesno azurirani poeni korisnika!")
                } else {
                    Resource.Failure(Exception("Korisnik ne postoji"))
                }
            } else {
                Resource.Failure(Exception("Korisnikov dokument ne postoji"))
            }
            Resource.Success("Uspesno dodati podaci o korisniku")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }






}