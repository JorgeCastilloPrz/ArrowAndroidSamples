package com.github.jorgecastillo.architecturecomponentssample.model.error

/**
 * This sealed class represents all the possible errors that the app is going to model inside its
 * domain. All the exceptions / errors provoked by third party libraries or APIs are mapped to any
 * of the types defined on this class.
 *
 * Mapping exceptions to errors allows the domain use case functions to be referentially
 * transparent, which means that they are completely clear and straightforward about what they
 * return just by reading their public function output types.
 *
 * Other approaches like exceptions + callback propagation (to be able to surpass thread limits)
 * bring not required complexity to the architecture introducing asynchronous semantics.
 */
sealed class CharacterError {
  class AuthenticationError : CharacterError()
  class NotFoundError : CharacterError()
  class UnknownServerError : CharacterError()
}
