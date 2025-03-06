package com.example.restorani

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.restorani.ui.theme.RestoraniTheme
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.restorani.view_models.AuthVM
import com.example.restorani.view_models.AuthVMFactory
import com.example.restorani.view_models.RestaurantViewModel
import com.example.restorani.view_models.RestaurantViewModelFactory


class MainActivity : ComponentActivity(){
    private val AuthVM: AuthVM by viewModels {
        AuthVMFactory()
    }

    private val restaurantViewModel: RestaurantViewModel by viewModels{
        RestaurantViewModelFactory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            RestaurantA(AuthVM,restaurantViewModel)
        }
    }
}