package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

import com.github.jorgecastillo.kotlinandroid.domain.Result
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.model.CharactersDto
import com.karumi.marvelapiclient.model.CharactersQuery
import com.karumi.marvelapiclient.model.MarvelResponse
import retrofit.Call

inline fun <T, U> Call<T>.unwrapCall(f: T.() -> U) = execute().body().f()

/*fun <E, T, U> Call<T>.asResult(f: T.() -> Disjunction<E, U>): Result<E, U> =
    Result.asyncOf {
      execute().body().f()
    }

fun <E, A> Call<A>.asyncResult(): Result<E, A> =
    Result.async {
      execute().body()
    }*/

fun <E> CharacterApiClient.asyncResult(
    query: CharactersQuery): Result<E, MarvelResponse<CharactersDto>> =
    Result.async {
      getAll(query)
    }
