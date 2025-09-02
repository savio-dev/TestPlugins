version = "1.0"

cloudstream {
    description = "Plugin para o site AnimesROLL"
    language = "pt-br"
    authors = listOf("Sephiroth")
    tvTypes = listOf("Anime")
    iconUrl = "https://www.anroll.net/favicon.ico"
}

// Task para gerar o JAR do plugin
tasks.register<Jar>("buildPluginJar") {
    archiveBaseName.set("AnimesROLLPlugin")
    archiveVersion.set("1.0")
    from(project.the<SourceSetContainer>()["main"].output)
    manifest {
        attributes["Main-Class"] = "com.example.animesroll.AnimesRollProvider"
    }
}
