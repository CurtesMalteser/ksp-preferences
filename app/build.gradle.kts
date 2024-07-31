import com.google.protobuf.gradle.id
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt)
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
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Fix for KSP
    // Non explicit type specification on @Provides causes error.NonExistentClass
    // https://github.com/google/dagger/issues/4051#issuecomment-1969345762
    // Alternative solution:
    // https://github.com/google/dagger/issues/4051#issuecomment-2260754486
    androidComponents {
        onVariants(selector().all()) { variant ->
            afterEvaluate {
                project.tasks.getByName("ksp" + variant.name.capitalized() + "Kotlin") {
                    val buildConfigTask =
                        project.tasks.getByName("generate${variant.name.capitalized()}Proto")
                                as com.google.protobuf.gradle.GenerateProtoTask
                    dependsOn(buildConfigTask)
                    (this as AbstractKotlinCompileTool<*>).setSource(buildConfigTask.outputBaseDir)
                }
            }
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    implementation(libs.lifecycle.runtime)
    implementation(libs.activity.compose)
    implementation(libs.hilt.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.datastore.preferences)
    implementation(libs.datastore)

    implementation(project(":annotation"))
    ksp(project(":processor"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

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
