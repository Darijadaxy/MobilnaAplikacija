package com.example.restorani.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
//import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restorani.app_navigation.Routes
import com.example.restorani.data.repositories.Resource
import com.example.restorani.logs.ExceptionLogs
import com.example.restorani.screens.components.SignUpInButton
import com.example.restorani.screens.components.PasswordInputField
import com.example.restorani.screens.components.InputField
import com.example.restorani.screens.components.CopyrightText
import com.example.restorani.screens.components.DashedLineBackground
import com.example.restorani.screens.components.Secondary
import com.example.restorani.screens.components.Header
import com.example.restorani.screens.components.InputFieldLabel
import com.example.restorani.screens.components.CallToActionText
import com.example.restorani.screens.components.customErrorContainer
import com.example.restorani.view_models.AuthVM

@Composable
fun SignInScreen ( authVM: AuthVM?, navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }

    val signInFlow = authVM?.signInFlow?.collectAsState()

    DashedLineBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 44.dp, bottom = 14.dp)
        )
        {
            Header(header_text = "Restaurant Review App!")

            Spacer(modifier = Modifier.height(9.dp))
            Secondary(secondary_text = "Pridružite se zajednici ljubitelja hrane")

            Spacer(modifier = Modifier.height(47.dp))
            InputFieldLabel(label = "Unesite vaš E-mail")

            Spacer(modifier = Modifier.height(2.dp))
            InputField(
                hint = "ime@domen.com",
                value = email,
                isEmail = true,
                isError = isEmailError,
                errorText = emailErrorText
            )

            Spacer(modifier = Modifier.height(20.dp))
            InputFieldLabel(label = "Unesite vašu lozinku")

            Spacer(modifier = Modifier.height(2.dp))
            PasswordInputField(
                inputValue = password,
                hint = "Pomešajte slova, brojeve i simbole za jaku lozinku",
                isError = isPasswordError,
                errorText = passwordErrorText
            )

            if (isError.value) customErrorContainer(errorText = "Greška! Neispravni podaci, molimo pokušajte ponovo.")
            Spacer(modifier = Modifier.height(24.dp))
            SignUpInButton(
                onClick = {
                    isEmailError.value = false
                    isPasswordError.value = false
                    isError.value = false
                    isLoading.value = true
                    authVM?.signIn(email.value, password.value)
                },

                text = "Prijavi se",
                textColor = Color.White,
                isEnabled = buttonIsEnabled,
                isLoading = isLoading,
            )

            Spacer(modifier = Modifier.height(17.dp))
            CallToActionText(
                promptText = "Novi ste ovde?  ",
                ctaLinkText = "Registrujte se",
                onClick = {
                    navController.navigate(Routes.signUpScreen)
                })

        }

        signInFlow?.value.let {
            when (it) {
                is Resource.Failure -> {
                    isLoading.value = false
                    Log.d("[ERROR]", it.exception.message.toString())
                    when (it.exception.message.toString()) {
                        ExceptionLogs.emptyFields -> {
                            isEmailError.value = true
                            isPasswordError.value = true
                        }

                        ExceptionLogs.badlyFormattedEmail -> {
                            isEmailError.value = true
                            emailErrorText.value = "Nevažeća email adresa."
                        }

                        ExceptionLogs.invalidCredentials -> {
                            isError.value = true
                            errorText.value = "Nevažeći podaci."
                        }

                        else -> {}
                    }

                }

                is Resource.Success -> {
                    isLoading.value = false
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.firstScreen) {
                            popUpTo(Routes.firstScreen) {
                                inclusive = true
                            }
                        }
                    }
                }

                is Resource.Loading -> {}
                null -> {}
                Resource.Loading -> TODO()
            }
        }
    }
}
