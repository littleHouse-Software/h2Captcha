plugins {
    id("java")
}

group = "com.littleHouse.h2Captcha"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.2captcha:2captcha-java:1.2.0")
}
