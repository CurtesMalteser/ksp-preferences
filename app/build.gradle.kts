import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt") // Remove and migrate to KSP since Hilt is supported on Kotlin 1.9.10+
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.google.protobuf)
}

android {

    namespace = "com.curtesmalteser.ksp.preferences"

    compileSdk = 35

    defaultConfig {
        applicationId = "com.curtesmalteser.ksp.preferences"
        minSdk = 29
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
        }
    }

    sourceSets.configureEach {
        kotlin.srcDir("${layout.buildDirectory}/generated/ksp/$name/kotlin/")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.core)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    implementation(libs.lifecycle.runtime)
    implementation(libs.activity.compose)
    implementation(libs.hilt.navigation.compose)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.datastore.preferences)
    implementation(libs.datastore)

    implementation(project(":annotation"))
    ksp(project(":processor"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.protobuf.lite)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.0.0"
    }
    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("javalite") {}
            }
        }
    }
}
