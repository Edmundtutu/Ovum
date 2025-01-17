plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.pac.ovum"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pac.ovum"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                        "room.schemaLocation" to "$projectDir/schemas".toString()
                )
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.fragment.testing)
    implementation(libs.activity)
    androidTestImplementation(project(":app"))
    androidTestImplementation(project(":app"))
    val room_version = "2.6.1"

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
//    implementation(libs.lifecycle.livedata.ktx)
//    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // Room components
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-paging:$room_version")

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

    // for netWorks shall use Volley
    implementation("com.android.volley:volley:1.2.1")

    //  The Chart library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

}

configurations.all {
    resolutionStrategy {
        // Force consistent versions across your project to avoid conflicts
        force ("androidx.core:core:1.13.0")
        force ("androidx.appcompat:appcompat:1.3.0")
        force ("androidx.recyclerview:recyclerview:1.2.1")
    }
}

// Apply compiler args for all Java compile tasks
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}
