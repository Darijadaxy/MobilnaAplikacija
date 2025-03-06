package com.example.restorani.app_navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.restorani.data.repositories.Resource
import com.example.restorani.screens.AddRestaurantScreen
//import com.example.restorani.screens.FilterScreen
import com.example.restorani.screens.MapScreen
import com.example.restorani.screens.RangScreen
import com.example.restorani.screens.RestaurantDetScreen
//import com.example.restorani.screens.Service
import com.example.restorani.screens.SignInScreen
import com.example.restorani.screens.SignUpScreen
import com.example.restorani.screens.TableScreen
import com.example.restorani.screens.FirstScreen
import com.example.restorani.view_models.AuthVM
import com.example.restorani.view_models.RestaurantViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Routing (
    authVM: AuthVM,
    restaurantViewModel : RestaurantViewModel

)
{
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.signInScreen) {

        composable(Routes.signInScreen) {
            SignInScreen(navController = navController, authVM = authVM)
        }
        composable(Routes.signUpScreen) {
            SignUpScreen(navController = navController, authVM = authVM)
        }
        composable(Routes.firstScreen) {
            FirstScreen(navController = navController, authVM = authVM)
        }
        composable(Routes.mapScreen) {
            MapScreen(
                //authVM = authVM,
                restaurantViewModel = restaurantViewModel,
                 navController = navController,
                cameraPositionState = rememberCameraPositionState(),
                myLocation = remember { mutableStateOf(null) }

            )
        }

        composable(Routes.tableScreen) {
            TableScreen(
               restaurantViewModel = restaurantViewModel
            )
        }
        composable(Routes.rangScreen) {
            RangScreen(
                restaurantViewModel = restaurantViewModel
            )
        }


        composable(
            "restaurantDet/{restaurantId}",
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId")
            val restaurantsResource = restaurantViewModel.restaurants.collectAsState().value

            when (restaurantsResource) {
                is Resource.Success -> {
                    val restaurant = restaurantsResource.result.find { it.id == restaurantId }
                    restaurant?.let {
                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                            RestaurantDetScreen(
                                restaurant = it,
                                onBack = { navController.popBackStack() },
                               onAddComment = { comment, uid ->
                                if (restaurantId != null) {
                                    restaurantViewModel.addComment(restaurantId, comment, uid)
                                }
                           },
                            onAddRating = { rating, uid ->
                               if (restaurantId != null) {
                                    restaurantViewModel.addRating(restaurantId, rating, uid)
                                }
                            },
                                uid=it1


                            )
                        }

                    }
                }
                is Resource.Loading -> {
                    // Handle loading state
                }
                is Resource.Failure -> {
                    // Handle failure state
                }
            }
        }


    }
}