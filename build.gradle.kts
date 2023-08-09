plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.2"
    id("io.micronaut.aot") version "4.0.2"
    id("io.micronaut.openapi") version "4.0.2"
}

version = "0.1"
group = "example.micronaut"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("jakarta.validation:jakarta.validation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("example.micronaut.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("example.micronaut.*")
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
    openapi {
        server(file("src/main/resources/missingjsonsubtypes.yml")) {
            apiPackageName = "com.example.openapi.api"
            modelPackageName = "com.example.openapi.model"
            useReactive = false
        }
    }
}

// FIX TO ADD JsonSubTypes ANNOTATION TO GENERATED BookInfo.java FILE.
// COMMENT OUT TO SEE TEST FAIL, IF IT IS NOT THERE!
tasks {
    named("generateServerOpenApiModels") {
        doLast {
            val file = File("$buildDir/generated/openapi/generateServerOpenApiModels/src/main/java/com/example/openapi/model/BookInfo.java")
            val content = file.readText()
            val updatedContent = content.replaceFirst(
                """@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
""",
                """@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DetailedBookInfo.class, name = "DETAILED"),
        @JsonSubTypes.Type(value = BasicBookInfo.class, name = "BASIC"),
})
"""
            )
            file.writeText(updatedContent)
        }
    }
}
// END OF FIX
