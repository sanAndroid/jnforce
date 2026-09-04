import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    // Java support
    id("java")
    // Kotlin support
    kotlin("jvm") version "2.1.20"
    // IntelliJ Platform Gradle Plugin
    id("org.jetbrains.intellij.platform") version "2.18.1"
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.5.0"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "2026.2.0"
    // Gradle Kover Plugin
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// IntelliJ Platform 2025.2 runs on JDK 21.
kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()

    // IntelliJ Platform Gradle Plugin Repositories Extension -> https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    // IntelliJ Platform Gradle Plugin Dependencies Extension -> https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion"),
        )

        bundledPlugins(
            providers.gradleProperty("platformBundledPlugins")
                .map { it.split(',').map(String::trim).filter(String::isNotEmpty) },
        )
        plugins(
            providers.gradleProperty("platformPlugins")
                .map { it.split(',').map(String::trim).filter(String::isNotEmpty) },
        )

        testFramework(TestFrameworkType.Platform)
        // Provides LightJavaCodeInsightFixtureTestCase, used by the settings tests.
        testFramework(TestFrameworkType.Plugin.Java)
    }

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.4")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    testImplementation("junit:junit:4.13.2")
}

// IntelliJ Platform Gradle Plugin Extension -> https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        // `name` is intentionally not patched here so the display name stays "JnForce" as declared
        // in plugin.xml; `pluginName` drives the distribution file name instead.
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased()).withHeader(false).withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            // Stay compatible with future IDE releases instead of pinning an upper bound.
            untilBuild = provider { null }
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map {
            listOf(it.split('-').getOrElse(1) { "default" }.split('.').first())
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }
}
