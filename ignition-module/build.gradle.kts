import io.ia.sdk.gradle.modl.task.SignModule

plugins {
    id("io.ia.sdk.modl") version "0.1.1"
}

val signingKeystore = providers.environmentVariable("PDFMODULE_KEYSTORE")
val signingKeystorePassword = providers.environmentVariable("PDFMODULE_KEYSTORE_PASSWORD")
val signingCertificate = providers.environmentVariable("PDFMODULE_CERTIFICATE")
val signingCertificatePassword = providers.environmentVariable("PDFMODULE_CERT_PASSWORD")
val signingAlias = providers.environmentVariable("PDFMODULE_ALIAS").orElse("pdfmodule")
val signingEnabled = listOf(
    signingKeystore,
    signingKeystorePassword,
    signingCertificate,
    signingCertificatePassword,
).all { it.isPresent }

allprojects {
    group = "com.adriansevelin.pdfmodule"
    version = "1.1.0"
}

subprojects {
    repositories {
        mavenCentral()
        maven(url = "https://nexus.inductiveautomation.com/repository/public/")
    }
}

ignitionModule {
    id.set("com.adriansevelin.pdfmodule")
    name.set("PDF Tool Module")
    moduleDescription.set("Adds system.pdf.downloadPDF, downloadAndRead, listPDF, readPDF, parseBetween, and parsePDF in Gateway scope")
    moduleVersion.set(project.version.toString())
    fileName.set("PDFParserModule.modl")
    requiredIgnitionVersion.set("8.3.0")

    projectScopes.set(
        mapOf(
            project(":gateway").path to "G",
        ),
    )

    hooks.set(
        mapOf(
            "com.adriansevelin.pdfmodule.PdfGatewayHook" to "G",
        ),
    )

    skipModlSigning.set(!signingEnabled)
}

tasks.named<SignModule>("signModule") {
    skipSigning.set(!signingEnabled)

    if (signingEnabled) {
        keystorePath.set(signingKeystore.get())
        keystorePw.set(signingKeystorePassword.get())
        certFilePath.set(signingCertificate.get())
        alias.set(signingAlias.get())
        certPw.set(signingCertificatePassword.get())
    }
}
