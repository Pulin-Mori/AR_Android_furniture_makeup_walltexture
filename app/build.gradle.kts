plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.app_2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app_2"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:+")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.gorisse.thomas.sceneform:sceneform:1.23.0")

    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-database:20.2.1")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")

    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-video:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")
    implementation("androidx.camera:camera-extensions:1.3.3")

    implementation("com.google.mediapipe:tasks-vision:0.10.14") //Do Not Upgrade to version 0.20230731 as it will rise an error at runtime which will try to access unaccessible memory.

    implementation("com.google.mlkit:face-mesh-detection:16.0.0-beta2")
    implementation("com.google.mlkit:object-detection:17.0.1")
    implementation("com.google.mlkit:face-detection:16.1.6")
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")

    implementation("org.tensorflow:tensorflow-lite:2.10.0")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.3.0")

    implementation("org.opencv:opencv:4.9.0")
}