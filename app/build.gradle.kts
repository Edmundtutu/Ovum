import org.gradle.internal.impldep.org.junit.experimental.categories.Categories

// App-level build.gradle.kts

plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.ovum"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ovum"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

//dependencies {
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    implementation(libs.annotation)
//    implementation("androidx.fragment:fragment:1.8.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
//    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
//    implementation("androidx.startup:startup-runtime:1.1.1")
//    implementation("androidx.multidex:multidex:2.0.1")
//    implementation("androidx.recyclerview:recyclerview:1.2.1")
//    implementation("androidx.activity:activity-ktx:1.9.0")
//    implementation("com.google.android.material:material:1.4.0")
//    implementation("androidx.paging:paging-runtime:3.0.0")
//
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
//    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.0")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
//    implementation("androidx.paging:paging-runtime-ktx:3.1.1")  // Use the latest version
//    implementation("androidx.paging:paging-rxjava3:3.1.1")
//    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
//    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//    implementation("com.android.volley:volley:1.2.1")
//    implementation("com.github.sundeepk:compact-calendar-view:3.0.0") {
//        exclude(group = "com.android.support")
//    }
//
//    implementation("com.github.skydoves:balloon:1.6.8")
//
//}
//
//configurations.all {
//    resolutionStrategy {
//        force("androidx.core:core:1.13.0")
//        force("androidx.appcompat:appcompat:1.3.0")
//        force("androidx.recyclerview:recyclerview:1.2.1")
//        // Add any other AndroidX versions you are using
//    }
//}
dependencies {
    // Core Libraries
    implementation ("androidx.core:core-ktx:1.13.0")
    implementation ("androidx.appcompat:appcompat:1.3.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Activity and RecyclerView
    implementation ("androidx.activity:activity-ktx:1.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    // Paging Library
    implementation ("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation ("androidx.paging:paging-rxjava3:3.1.1")

    // Lifecycle Components (ViewModel, LiveData)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")

    // RxJava (for reactive programming)
    implementation ("io.reactivex.rxjava3:rxjava:3.0.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")

    // Compact Calendar View
    implementation("com.github.sundeepk:compact-calendar-view:3.0.0") {
        exclude(group = "com.android.support")  // Avoid conflicts with older Android libraries
    }

    // Skydoves Balloon Library (for tooltip bubbles)
    implementation ("com.github.skydoves:balloon:1.6.8"){
        exclude (group = "androidx.lifecycle", module = "lifecycle-runtime-ktx")
        exclude (group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    }

    //  The Chart library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // for netWorks shall use Volley
    implementation("com.android.volley:volley:1.2.1")

    // Test Dependencies
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}

configurations.all {
    resolutionStrategy {
        // Force consistent versions across your project to avoid conflicts
        force ("androidx.core:core:1.13.0")
        force ("androidx.appcompat:appcompat:1.3.0")
        force ("androidx.recyclerview:recyclerview:1.2.1")
    }
}
