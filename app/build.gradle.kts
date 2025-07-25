plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.isra2.desasolve2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.isra2.desasolve2"
        minSdk = 26
        targetSdk = 35
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
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Extended Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.2")
    
    // Camera X (comentado temporalmente para evitar crashes)
    // implementation("androidx.camera:camera-camera2:1.3.1")
    // implementation("androidx.camera:camera-lifecycle:1.3.1")
    // implementation("androidx.camera:camera-view:1.3.1")
    
    // Location (comentado temporalmente para evitar crashes)
    // implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // Date/Time picker (comentado temporalmente para evitar crashes)
    // implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.2.0")
    // implementation("com.maxkeppeler.sheets-compose-dialogs:calendar:1.2.0")
    // implementation("com.maxkeppeler.sheets-compose-dialogs:clock:1.2.0")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Retrofit para comunicación con API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}