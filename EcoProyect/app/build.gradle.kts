plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.ecoproyect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecoproyect"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        enable = true
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.+") //Gifs
    implementation("androidx.cardview:cardview:1.0.0") //Cardviews
    implementation("com.android.volley:volley:1.2.1") //Llamadas externas
    //implementation ("com.google.android.gms:play-services-auth:20.7.0")
    //implementation ("com.google.android.gms:play-services-auth-api-phone:18.0.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.3")
    implementation("androidx.navigation:navigation-ui:2.7.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")

    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    //implementation ("com.google.android.gms:play-services-location:18.0.0")

    implementation(project(":OSMBonusPack"))
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.6@aar")
    implementation ("org.mapsforge:mapsforge-map-android:0.13.0")
    implementation ("org.mapsforge:mapsforge-map:0.13.0")
    implementation ("org.mapsforge:mapsforge-themes:0.13.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}