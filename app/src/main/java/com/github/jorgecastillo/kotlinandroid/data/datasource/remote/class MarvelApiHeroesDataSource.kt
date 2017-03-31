package com.github.jorgecastillo.kotlinandroid.data.datasource.remote

class MarvelApiHeroesDataSource {

  // TODO uncomment for second phase
  /*fun getAll() = Reader<GetHeroesContext, Result<Error.HeroesNotFound, NonEmptyList<SuperHero>>> {
    binding {
      val query = CharactersQuery.Builder.create().withOffset(0).withLimit(10).build()
      val characterDTOs = bind(it.marvelApiClient.asyncResult(query))

      val superHeroes = characterDTOs.response.characters.map { mapApiCharacterToSuperHero(it) }

      val heroesResult: Result<Error.HeroesNotFound, NonEmptyList<SuperHero>> =
          if (superHeroes.isEmpty()) {
            Error.HeroesNotFound().raiseError()
          } else {
            NonEmptyList.unsafeFromList(superHeroes).result()
          }
      yields(bind(heroesResult))
    }
  }*/
}

