plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.doccur"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.doccur"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("androidx.compose.material3:material3:1.1.2") // or latest version
    implementation ("androidx.compose.ui:ui:1.5.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.common.android)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    val room_version = "2.6.0"
    implementation ("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    testImplementation ("androidx.room:room-testing:$room_version")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation ("androidx.compose.material:material-icons-extended:1.5.0")
    implementation ("io.coil-kt:coil-compose:2.4.0") // For image loading
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")





}