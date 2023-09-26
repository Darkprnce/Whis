import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}
val properties = Properties()
properties.load(project.rootProject.file("local.properties").reader())

inline fun <reified ValueT> com.android.build.api.dsl.VariantDimension.buildConfigField(
    name: String,
    value: ValueT
) {
    val resolvedValue = when (value) {
        is String -> "\"$value\"" // hate this
        else -> value
    }.toString()
    buildConfigField(ValueT::class.java.simpleName, name, resolvedValue)
}

android {
    namespace = "com.whis"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.whis"
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
        debug {
            buildConfigField("BASE_URL", properties.getProperty("BASE_URL"))
            buildConfigField("API_KEY", properties.getProperty("API_KEY"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.46.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.6")

    //Pager
    implementation("com.google.accompanist:accompanist-pager:0.29.1-alpha")

    //Coil
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil-gif:2.0.0-rc02")

    //Landscapist (Image Loading)
    implementation ("com.github.skydoves:landscapist-glide:2.2.9")

    //Room
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")

    //Livedata
    implementation("androidx.compose.runtime:runtime-livedata:1.5.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0")

    //Reorder LazyColumn
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    //Logging
    implementation("com.jakewharton.timber:timber:5.0.1")
}