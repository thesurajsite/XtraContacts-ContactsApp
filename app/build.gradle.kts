plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
   // alias(libs.plugins.google.gms.google.services)
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.surajverma.xtracontacts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.surajverma.xtracontacts"
        minSdk = 26
        targetSdk = 35
        versionCode = 16
        versionName = "1.1.6"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // viewmodel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    // FireStore
    implementation ("com.google.firebase:firebase-auth:23.2.1")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-firestore:25.1.4")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // in-app update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.6.6")

    // coil
    implementation("io.coil-kt:coil:2.5.0")

    //SDP
    implementation ("com.intuit.sdp:sdp-android:1.1.1")

}