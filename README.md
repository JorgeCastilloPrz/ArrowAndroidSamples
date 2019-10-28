Arrow Android Samples
=====================
[![Build Status](https://travis-ci.org/JorgeCastilloPrz/ArrowAndroidSamples.svg?branch=improve-reader-usage)](https://travis-ci.org/JorgeCastilloPrz/ArrowAndroidSamples)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3.50-blue.svg)](http://kotlinlang.org/)

<img height="100" src="https://avatars2.githubusercontent.com/u/29458023?v=4&amp;s=200" width="100">

Functional Programing Android architecture approaches using [Arrow](http://arrow-kt.io/)

# How to import it

The library being used here to fetch super heroes is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid) 
from [Karumi](https://github.com/Karumi). Since it's targeting the real [Marvel API](https://developer.marvel.com/), 
you will need to add `marvelPublicKey=your_public_key` and `marvelPrivateKey=your_private_key` to 
your home `gradle.properties` to be able to compile or run it. You can also add them by command line: 

`./gradlew detektCheck build -PmarvelPublicKey="\"whatever\"" -PmarvelPrivateKey="\"whatever\""`

# Main framework being used

To achieve **functional programing** over Kotlin I am using a library that we have been working on 
in the spanish dev community. It's called [Arrow](https://github.com/arrow-kt/arrow) and its first
official release is around the corner!

Big thanks to all the lib contributors which I am part of. [Here they are](https://github.com/arrow-kt/arrow/graphs/contributors).

# Approach used

This project uses the so called `Tagless-Final` style. Tagless-Final is focused on never depending on concrete types like Option or Try, but use Higher Kinds to make our code depend on typeclass constrained behaviors, leaving the decision about which concrete types to use to the moment when we want to run the code, or in other words, the "Runtime".

## IO

Once you're at the "edge of the world" to run your program, you'll pass a proper runtime. We're fixing the `F` type that the program remains agnostic of to be `IO`.

Attributions
------------
The library being used here to fetch super heroes is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid) 
from [Karumi](https://github.com/Karumi).

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://twitter.com/jorgecastillopr">
  <img alt="Twitter profile" src="https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/blob/master/assets/twitter_logo.png" />
</a>
<a href="https://medium.com/@jorgecastillopr">
  <img alt="Medium blog" src="https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/blob/master/assets/medium_blog_logo.png" />
</a>

License
-------

    Copyright 2018 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
