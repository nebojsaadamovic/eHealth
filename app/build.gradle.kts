plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("maven-publish")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.nites.ezdravljeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nites.ezdravljeapp"
        minSdk = 26
        targetSdk = 33
        versionCode = 2
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

    productFlavors {
        flavorDimensions("version")

        create("production") {
            applicationId = "com.nites.ezdravljeapp"
            dimension = "version"
            buildConfigField("String", "BASE_URL", "\"https://heporapp.nites.rs\"")
            buildConfigField(
                "String",
                "BASE_URL_MOCK",
                "\"https://ebcb0ca7-ab12-41cf-8dda-d91f6005c4df.mock.pstmn.io\""
            )
            manifestPlaceholders["icon"] = "@drawable/ic_logo"
            manifestPlaceholders["deepLinkUrl1"] = "\"https://heporapp.nites.rs/\""
            manifestPlaceholders["deepLinkUrl2"] = "\"https://heporapp.nites.rs/\""
            manifestPlaceholders["appName"] = "E-Zdravlje Prod"
            copy {
                from("src/qa/")
                include("*.json")
                into(".")
            }
        }

        create("qa") {
            applicationId = "com.nites.ezdravljeapp"
            dimension = "version"
            buildConfigField("String", "BASE_URL", "\"https://heporapp.nites.rs/\"")
            buildConfigField(
                "String",
                "BASE_URL_MOCK",
                "\"https://ebcb0ca7-ab12-41cf-8dda-d91f6005c4df.mock.pstmn.io\""
            )
            manifestPlaceholders["icon"] = "@drawable/ic_logo"
            manifestPlaceholders["deepLinkUrl1"] = "\"https://heporapp.nites.rs/\""
            manifestPlaceholders["deepLinkUrl2"] = "\"https://heporapp.nites.rs/\""
            manifestPlaceholders["appName"] = "E-Zdravlje Qa"
            copy {
                from("src/qa/")
                include("*.json")
                into(".")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.android.material:material:1.12.0")

    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //QR Code Scanner
    implementation("com.github.yuriy-budiyev:code-scanner:2.3.2")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //Kotlin reflection
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

    //Coil
    implementation("io.coil-kt:coil:2.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.camera:camera-camera2:1.3.4")

    // chunk - http debugging
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    //JWT
    implementation("com.auth0.android:jwtdecode:2.0.0")

    // Google Cast
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")

    // Swipe layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //  Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //google
    implementation("com.google.android.gms:play-services-fitness:21.2.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")


    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.8")
    implementation("com.google.firebase:firebase-config-ktx:21.1.2")
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")

    //AVC
    implementation("android:avcsdk:5.2.15")
    implementation("io.insert-koin:koin-core:3.2.2")

    //Live Kit
    implementation("io.livekit:livekit-android:2.7.0")
    implementation("com.github.lisawray.groupie:groupie:2.9.0")
    implementation("com.github.lisawray.groupie:groupie-viewbinding:2.9.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.mikhaellopez:circularimageview:4.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.github.mhiew:material-calendarview:2.0.3")

    implementation ("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")

}