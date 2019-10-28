Arrow Android Samples
=====================
[![Build Status](https://travis-ci.org/JorgeCastilloPrz/ArrowAndroidSamples.svg?branch=improve-reader-usage)](https://travis-ci.org/JorgeCastilloPrz/ArrowAndroidSamples)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3.50-blue.svg)](http://kotlinlang.org/)
[![Arrow version badge](https://img.shields.io/badge/arrow-0.10.2-blue.svg)](http://arrow-kt.io/)

<img height="100" src="https://avatars2.githubusercontent.com/u/29458023?v=4&amp;s=200" width="100">

Functional Programing Android architecture approaches using [Arrow](http://arrow-kt.io/).

# How to import it

The library being used here to fetch super newsItems is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid)
from [Karumi](https://github.com/Karumi). Since it's targeting the real [Marvel API](https://developer.marvel.com/), 
you will need to add `marvelPublicKey=your_public_key` and `marvelPrivateKey=your_private_key` to 
your home `gradle.properties` to be able to compile or run it. You can also add them by command line: 

`./gradlew detektCheck build -PmarvelPublicKey="\"whatever\"" -PmarvelPrivateKey="\"whatever\""`

# Approach used

This project uses the so called `Tagless-Final` Functional Programming style. Tagless-Final is focused on never depending on concrete data types like Option or Try, but make the program depend on an unknown `F` type end to end instead. Then we use Type Class constraints (behaviors) that work over that `F` to encode our programs, leaving the decision about which concrete type to use (or in other words: what type fix `F` to) to the moment when we are ready to run the code, or in other words, the "Runtime".

## IO

Once you're at the "edge of the world" to run your program, you'll pass a proper runtime. In this sample we are fixing the `F` type that the program remains agnostic of to be `IO`. But it could actually be any type that supports asynchrony and concurrency, since those are the only constraints we have for this program.

Attributions
------------
The library being used here to fetch super newsItems is the [MarvelApiClientAndroid](https://github.com/Karumi/MarvelApiClientAndroid)
from [Karumi](https://github.com/Karumi).

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://twitter.com/jorgecastillopr">
  <img alt="Twitter profile" src="https://github.com/JorgeCastilloPrz/KotlinAndroidFunctional/blob/master/assets/twitter_logo.png" />
</a>

For more Functional Programming content applied to Android you can [have a look at my blog](https://jorgecastillo.dev) 👍

License
-------

    Copyright 2019 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
