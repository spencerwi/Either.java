plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "com.spencerwi"
version = "2.2.0"

base.archivesBaseName = "Either.java"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenCentral()
}

dependencies {
	testCompile("org.assertj:assertj-core:3.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
}
tasks.withType<Test> { // Use JUnit 5 
	useJUnitPlatform()
}

tasks.register<Jar>("sourcesJar"){
    classifier = "sources"
    from(sourceSets.main.get().allJava)
}
tasks.register<Jar>("javadocJar"){
    classifier = "javadoc"
    from(tasks.javadoc.get().destinationDir)
}

buildScan {
	termsOfServiceUrl = "https://gradle.com/terms-of-service"
	termsOfServiceAgree = "yes"
}


if (!(project.hasProperty("isTravis"))){

    val sonatypeUsername : String by extra
    val sonatypePassword : String by extra

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = "Either.java"
                from(components["java"])
                artifact(tasks["sourcesJar"])
                artifact(tasks["javadocJar"])

                pom {
                    name.set("Either.java")
                    description.set("A right-biased implementation of \"Either a b\" for Java, using Java 8 for mapping/folding and type inference.")
                    url.set("http://github.com/spencerwi/Either.java")

                    scm {
                        url.set("scm:git@github.com:spencerwi/Either.java.git")
                        connection.set("scm:git@github.com:spencerwi/Either.java.git")
                        developerConnection.set("scm:git@github.com:spencerwi/Either.java.git")
                    }

                    licenses {
                        license {
                            name.set("The MIT License (MIT)")
                            url.set("https://raw.githubusercontent.com/spencerwi/Either.java/master/LICENSE")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("spencerwi")
                            name.set("Spencer Williams")
                        }
                    }
                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/spencerwi/Either.java/issues")
                    }
                }
            }
        }
        repositories {
            maven {
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
    }
    signing {
        sign(publishing.publications["mavenJava"])
    }
}
