plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven(url = "https://nexus.inductiveautomation.com/repository/public/")
}

dependencies {
    // Ignition APIs are provided by the platform at runtime.
    compileOnly("com.inductiveautomation.ignitionsdk:gateway-api:8.3.0")

    // Include PDFBox inside the module so no manual jar drop is required.
    modlImplementation("org.apache.pdfbox:pdfbox-app:2.0.30")
}
