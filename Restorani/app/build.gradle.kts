plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.restorani"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.restorani"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.play.services.cast.framework)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.compose.material3:material3")
   implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("com.google.code.gson:gson:2.8.6")

    //
   // implementation("androidx.compose.material3:material3:1.2.0") // Osvežena verzija material3
   // implementation("io.coil-kt:coil-compose:2.7.0")




    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))


    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.31.5-beta")




    //chat
    // Jetpack Compose dependencies
    //implementation("androidx.compose.material3:material3:1.2.0") // Ažurirano na najnoviju verziju
    //implementation("androidx.compose.material:material:1.6.7") // Za Material2, ako koristiš
    //implementation("androidx.compose.material:material-icons-core:1.6.7") // Za Material2 ikone
   // implementation("androidx.compose.material:material-icons-extended:1.6.7") // Za dodatne ikone

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.8.6")

    // Firebase BOM for Firebase dependencies
   // implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase dependencies (example)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    implementation("androidx.compose.material3:material3:<latest-version>")
}