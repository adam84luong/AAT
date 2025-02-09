plugins {
    id ("java-library")
}


repositories {
    google()
    mavenCentral()
}


tasks.test {
    useJUnitPlatform()
}


dependencies {
    val focVersion : String by project
    api("com.github.bailuk.foc:foc:$focVersion")


    // MapsForge Core
    val mapsForgeVersion: String by project
    implementation("org.mapsforge:mapsforge-core:$mapsForgeVersion")
    implementation("org.mapsforge:mapsforge-map:$mapsForgeVersion")


    /**
     *  Notnull annotation
     */
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    /**
     *  https://mvnrepository.com/artifact/net.sf.kxml/kxml2
     *  xml parser implementation
     */
    implementation("net.sf.kxml:kxml2:2.3.0")


    /**
     *  https://mvnrepository.com/artifact/org.apache.commons/commons-text
     *  To escape html
     */
    implementation("org.apache.commons:commons-text:1.9")

    /**
     *
     *   https://junit.org/junit5/docs/current/user-guide/#dependency-metadata
     *   https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
     *
     */
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")


    // open-location-code
    implementation("com.google.openlocationcode:openlocationcode:1.0.4")
}