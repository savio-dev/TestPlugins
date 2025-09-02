package com.example.animesroll

import com.lagradost.cloudstream3.*
import org.jsoup.nodes.Element

class AnimesRollProvider : MainAPI() {
    override var mainUrl = "https://www.anroll.net"
    override var name = "AnimesROLL"
    override val hasMainPage = true
    override var lang = "pt"
    override val supportedTypes = setOf(TvType.Anime)

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val document = app.get(mainUrl).document

        // Últimos episódios
        val latest = document.select("div.edmaGy").mapNotNull { element ->
            element.toSearchResult()
        }

        // Lista de animes
        val animeList = document.select("div.jTVCGa").mapNotNull { element ->
            element.toSearchResult()
        }

        return newHomePageResponse(
            list = listOf(
                HomePageList("Últimos Episódios", latest),
                HomePageList("Lista de Animes", animeList)
            )
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=$query"
        val document = app.get(url).document
        return document.select("div.jTVCGa").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text() ?: ""
        val poster = document.selectFirst("img")?.attr("src")

        val episodes = document.select("div.itemlistepisode a").mapIndexed { index, ep ->
            Episode(
                data = ep.attr("href"),
                name = ep.text().ifBlank { "Episódio ${index + 1}" }
            )
        }

        return AnimeLoadResponse(
            name = title,
            url = url,
            type = TvType.Anime,
            episodes = episodes
        ).apply {
            posterUrl = poster
        }
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val link = selectFirst("a")?.attr("href") ?: return null
        val title = selectFirst("h1")?.text() ?: return null
        val poster = selectFirst("img")?.attr("src")
        return AnimeSearchResponse(
            name = title,
            url = link,
            type = TvType.Anime
        ).apply {
            posterUrl = poster
        }
    }
}
