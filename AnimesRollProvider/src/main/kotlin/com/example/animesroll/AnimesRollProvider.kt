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
            element.toSearchResult(isEpisode = true)
        }

        // Lista de animes
        val animeList = document.select("div.jTVCGa").mapNotNull { element ->
            element.toSearchResult()
        }

        return HomePageResponse(
            listOf(
                HomePageList("Últimos Episódios", latest),
                HomePageList("Lista de Animes", animeList)
            ),
            hasNext = false
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
            newEpisode {
                this.url = ep.attr("href")
                this.name = ep.selectFirst(".titulo_episodio")?.text() ?: "Episódio ${index + 1}"
                this.episode = index + 1
            }
        }

        return AnimeLoadResponse(
            name = title,
            url = url,
            apiName = name,
            type = TvType.Anime,
            episodes = episodes,
            posterUrl = poster
        )
    }

    private fun Element.toSearchResult(isEpisode: Boolean = false): SearchResponse? {
        val link = selectFirst("a")?.attr("href") ?: return null
        val title = selectFirst("h1")?.text() ?: return null
        val poster = selectFirst("img")?.attr("src")

        return AnimeSearchResponse(
            name = title,
            url = link,
            apiName = name,
            type = TvType.Anime,
            posterUrl = poster
        ).apply {
            if (isEpisode) {
                addDubStatus(dubExist = false, subExist = true)
            }
        }
    }
}
