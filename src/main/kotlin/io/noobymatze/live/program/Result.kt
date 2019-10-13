package io.noobymatze.live.program


sealed class Result<out E, out A> {
    data class Success<out E, out A>(val value: A): Result<E, A>()
    data class Failure<out E, out A>(val error: E): Result<E, A>()
}