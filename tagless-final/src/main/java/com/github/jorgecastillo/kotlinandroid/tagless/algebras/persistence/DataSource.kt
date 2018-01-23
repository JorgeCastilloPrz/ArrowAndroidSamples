package com.github.jorgecastillo.kotlinandroid.tagless.algebras.persistence

import arrow.HK
import arrow.TC
import arrow.data.Try
import arrow.effects.Async
import arrow.syntax.either.right
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

@typeclass
interface DataSource<F> : TC {

    fun AC(): Async<F>

    private val apiClient
        get() = CharacterApiClient(MarvelApiConfig.Builder(
                BuildConfig.MARVEL_PUBLIC_KEY,
                BuildConfig.MARVEL_PRIVATE_KEY).debug().build())

    private fun buildFetchHeroesQuery(): CharactersQuery =
            CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()

    private fun fetchHero(heroId: String) =
            apiClient.getCharacter(heroId).response

    private fun fetchHeroes(query: CharactersQuery): List<CharacterDto> =
            apiClient.getAll(query).response.characters

    private fun <F, A, B> runInAsyncContext(
            f: () -> A,
            onError: (Throwable) -> B,
            onSuccess: (A) -> B, AC: Async<F>): HK<F, B> {
        return AC.async { proc ->
            async(CommonPool) {
                val result = Try { f() }.fold(onError, onSuccess)
                proc(result.right())
            }
        }
    }

    fun fetchAllHeroes(): HK<F, List<CharacterDto>> =
        AC().binding {
            val query = buildFetchHeroesQuery()
            runInAsyncContext(
                    f = { fetchHeroes(query) },
                    onError = { AC().raiseError<List<CharacterDto>>(it) },
                    onSuccess = { AC().pure(it) },
                    AC = AC()
            ).bind()
        }


    fun fetchHeroDetails(heroId: String): HK<F, CharacterDto> =
            AC().binding {
                runInAsyncContext(
                        f = { fetchHero(heroId) },
                        onError = { AC().raiseError<CharacterDto>(it) },
                        onSuccess = { AC().pure(it) },
                        AC = AC()
                ).bind()
            }
}
