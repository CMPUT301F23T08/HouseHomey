
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.8"
}

android {
    namespace = "com.example.househomey"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.househomey"
        minSdk = 34

        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
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
            //isTestCoverageEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "jdk/**",
        "jdk.internal.*"
    )

    val debugTree = fileTree(mapOf("dir" to file("${buildDir}/intermediates/classes/debug"), "excludes" to fileFilter))
    val mainSrc = file("${project.projectDir}/src/main/java")

    sourceDirectories.from(mainSrc)
    classDirectories.from(debugTree)
    executionData.from(file("${buildDir}/jacoco/testDebugUnitTest.exec"))
}