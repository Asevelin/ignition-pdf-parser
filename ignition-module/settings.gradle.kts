pluginManagement {
	repositories {
		gradlePluginPortal()
		maven(url = "https://nexus.inductiveautomation.com/repository/public/")
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
	repositories {
		mavenCentral()
		maven(url = "https://nexus.inductiveautomation.com/repository/public/")
	}
}

rootProject.name = "pdf-parser-module"

include("gateway")
