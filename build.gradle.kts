plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.2"
    id("io.micronaut.aot") version "4.4.2"
}

version = "0.1"
group = "vehicle.status.service"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.projectreactor:reactor-core:3.7.0")
    implementation("io.micronaut:micronaut-retry")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    implementation("io.micronaut.problem:micronaut-problem-json")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("com.github.tomakehurst:wiremock:3.0.1")
    testImplementation("org.wiremock:wiremock-standalone:3.2.0")
    testImplementation("io.projectreactor:reactor-test:3.7.0")

}


application {
    mainClass = "vehicle.status.service.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("vehicle.status.service.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}

