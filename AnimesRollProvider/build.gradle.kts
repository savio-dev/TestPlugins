version = 1

cloudstream {
    description = "Plugin para o site AnimesROLL"
    language = "pt-br"
    authors = listOf("Sephiroth")
    tvTypes = listOf("Anime")
    iconUrl = "https://www.anroll.net/favicon.ico"
}

import org.gradle.jvm.tasks.Jar

// Task para gerar o JAR do plugin
tasks.register<Jar>("buildPluginJar") {
    archiveBaseName.set("AnimesROLLPlugin") // nome do JAR
    archiveVersion.set("1.0")               // versão do JAR
    from(sourceSets.main.get().output)      // inclui código compilado
    manifest {
        attributes["Main-Class"] = "com.example.animesroll.AnimesRollProvider"
    }
}
