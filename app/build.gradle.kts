import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.map1"
    compileSdk = 34

    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/NOTICE.md")
    }

    defaultConfig {
        applicationId = "com.example.map1"
        minSdk = 26
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
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        viewBinding = true
    }


}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.osmdroid.android)
    implementation(libs.osmbonuspack)
    implementation(libs.graphhopper.core)
    implementation(libs.geojson.jackson)
    implementation(libs.commons.collections4)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.touchimageview)
    coreLibraryDesugaring (libs.desugar.jdk.libs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}