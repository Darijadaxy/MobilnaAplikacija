package com.example.restorani.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.restorani.screens.components.UploadIcon
import com.example.restorani.screens.components.CallToActionText
import com.example.restorani.view_models.AuthVM

@Composable
fun SignUpScreen(authVM: AuthVM?, navController: NavController?) {
    val signUpFlow = authVM?.signUpFlow?.collectAsState()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val profileImage = remember { mutableStateOf(Uri.EMPTY) }

    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val isImageError = remember { mutableStateOf(false) }
    val isFullNameError = remember { mutableStateOf(false) }
    val isPhoneNumberError = remember { mutableStateOf(false) }

    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }

    DashedLineBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Header(header_text = "Restaurant Review App !")

            Spacer(modifier = Modifier.height(8.dp))
            Secondary(secondary_text = "Kreirajte svoj nalog kako biste istražili i podelili najbolje restorane...")

            Spacer(modifier = Modifier.height(34.dp))
            InputFieldLabel(label = "Profilna slika")

            Spacer(modifier = Modifier.height(2.dp))
            UploadIcon(profileImage, isImageError)

            Spacer(modifier = Modifier.height(16.dp))
            InputFieldLabel(label = "Puno ime")

            Spacer(modifier = Modifier.height(2.dp))
            InputField(
                hint = "Darija Denic",
                value = fullName,
                isEmail = false,
                isError = isFullNameError,
                errorText = emailErrorText
            )

            Spacer(modifier = Modifier.height(16.dp))
            InputFieldLabel(label = "Email adresa")

            Spacer(modifier = Modifier.height(2.dp))
            InputField(
                hint = "ime@domen.com",
                value = email,
                isEmail = true,
                isError = isEmailError,
                errorText = emailErrorText
            )

            Spacer(modifier = Modifier.height(16.dp))
            InputFieldLabel(label = "Broj telefona")

            Spacer(modifier = Modifier.height(2.dp))
            InputField(
                hint = "+381604263509",
                value = phoneNumber,
                isEmail = false,
                isError = isPhoneNumberError,
                errorText = emailErrorText
            )

            Spacer(modifier = Modifier.height(16.dp))
            InputFieldLabel(label = "Lozinka")

            Spacer(modifier = Modifier.height(2.dp))
            PasswordInputField(
                inputValue = password,
                hint = "Pomešajte slova, brojeve i simbole za jaku lozinku",
                isError = isPasswordError,
                errorText = passwordErrorText
            )

            Spacer(modifier = Modifier.height(24.dp))
            SignUpInButton(
                onClick = {
                    isImageError.value = false
                    isEmailError.value = false
                    isPasswordError.value = false
                    isFullNameError.value = false
                    isPhoneNumberError.value = false
                    isError.value = false
                    isLoading.value = true

                    if (profileImage.value == Uri.EMPTY) {
                        isImageError.value = true
                        isLoading.value = false
                    } else if (fullName.value.isEmpty()) {
                        isFullNameError.value = true
                        isLoading.value = false
                    } else if (email.value.isEmpty()) {
                        isEmailError.value = true
                        isLoading.value = false
                    } else if (phoneNumber.value.isEmpty()) {
                        isPhoneNumberError.value = true
                        isLoading.value = false
                    } else if (password.value.isEmpty()) {
                        isPasswordError.value = true
                        isLoading.value = false
                    } else {
                        authVM?.signUp(
                            profileImage = profileImage.value,
                            fullName = fullName.value,
                            email = email.value,
                            phoneNumber = phoneNumber.value,
                            password = password.value
                        )
                    }
                },
                text = "Registrujte se",
                isEnabled = buttonIsEnabled,
                isLoading = isLoading,
                textColor = Color.White
            )

            Spacer(modifier = Modifier.height(17.dp))
            CallToActionText(
                promptText = "Već imate nalog? ",
                ctaLinkText = "Prijavite se",
                onClick = {
                    navController?.navigate(Routes.signInScreen)
                }
            )

            Spacer(modifier = Modifier.height(101.dp))
            CopyrightText(
                year = 2024,
                owner = "RestoranApp",
                textColor = Color.Gray,
                modifier = Modifier
                    .align(Alignment.End)
                    .align(Alignment.CenterHorizontally)
            )
        }

        signUpFlow?.value?.let {
            when (it) {
                is Resource.Failure -> {
                    isLoading.value = false
                    Log.e("[ERROR]", it.exception.message.toString())

                    when (it.exception.message.toString()) {
                        ExceptionLogs.emptyFields -> {
                            isEmailError.value = true
                            isPasswordError.value = true
                        }

                        ExceptionLogs.badlyFormattedEmail -> {
                            isEmailError.value = true
                            emailErrorText.value = "Email adresa nije ispravno formatirana."
                        }

                        ExceptionLogs.invalidCredentials -> {
                            isError.value = true
                            errorText.value = "Uneti podaci su neispravni ili su istekli."
                        }

                        ExceptionLogs.passwordTooShort -> {
                            isPasswordError.value = true
                            passwordErrorText.value = "Lozinka mora imati najmanje 6 karaktera."
                        }

                        ExceptionLogs.emailAlreadyInUse -> {
                            isError.value = true
                            errorText.value = "Ova email adresa je već povezana sa drugim nalogom."
                        }

                        else -> { }
                    }
                }
                is Resource.Success -> {
                    isLoading.value = false
                    LaunchedEffect(Unit) {
                        navController?.navigate(Routes.firstScreen) {
                            popUpTo(Routes.firstScreen) {
                                inclusive = true
                            }
                        }
                    }
                }
                is Resource.Loading -> { }

                null -> Log.d("SignUpScreen", "SignUp flow ne postoji!")
                Resource.Loading -> TODO()
            }
        }
    }
}
