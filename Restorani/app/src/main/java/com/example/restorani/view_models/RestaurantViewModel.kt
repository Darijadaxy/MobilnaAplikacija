package com.example.restorani.view_models

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restorani.data.models.Restaurant
import com.example.restorani.data.models.User
import com.example.restorani.data.repositories.Resource
import com.example.restorani.data.repositories.RestaurantRepoImpl
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestaurantViewModel : ViewModel() {

    val repository = RestaurantRepoImpl()
    private val _restaurantFlow = MutableStateFlow<Resource<String>?>(null)
    val restaurantFlow: StateFlow<Resource<String>?> = _restaurantFlow
    private val _restaurants = MutableStateFlow<Resource<List<Restaurant>>>(Resource.Success(emptyList()))
    val restaurants: StateFlow<Resource<List<Restaurant>>> get() = _restaurants
   // private val _userRestaurants = MutableStateFlow<Resource<List<Restaurant>>>(Resource.Success(emptyList()))
   // val userRestaurants: StateFlow<Resource<List<Restaurant>>> get() = _userRestaurants
    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Success(emptyList()))
    val users: StateFlow<Resource<List<User>>> get() = _users


    init {
        getAllRestaurants()
        getAllUsers()
    }
    fun getAllRestaurants() = viewModelScope.launch {
        _restaurants.value = repository.getAllRestaurants()
    }
    fun getAllUsers() = viewModelScope.launch {
        _users.value = repository.getAllUsers()
    }

    fun saveRestaurant(
        location: LatLng,
        name: String,
        description: String,
        restaurantImages: List<Uri>,
        date: Timestamp
    ) = viewModelScope.launch {
        _restaurantFlow.value = Resource.Loading
        val result = repository.saveRestaurant(
            location = location,
            name = name,
            description = description,
            restaurantImages = restaurantImages,
            date = date
        )
        if (result is Resource.Success) {
            getAllRestaurants()
        }
        _restaurantFlow.value = result
    }

    fun addComment(restaurantId: String, comment: String,uid: String) = viewModelScope.launch {
        _restaurantFlow.value = Resource.Loading
        val result = repository.addCommentToRestaurant(restaurantId, comment,uid)
        if (result is Resource.Success) {
            getAllRestaurants()
        }
        _restaurantFlow.value = result
    }

    fun addRating(restaurantId: String, rating: Int,uid: String,) = viewModelScope.launch {
        _restaurantFlow.value = Resource.Loading
        val result = repository.addRatingToRestaurant(restaurantId, rating, uid)
        if (result is Resource.Success) {
            getAllRestaurants()
        }
        _restaurantFlow.value = result
    }


}

class RestaurantViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RestaurantViewModel::class.java)){
            return RestaurantViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}