package com.nsergio.dev.realtimebasico.extensions

object Extensions {
    fun <T> Iterable<T>.diffAndAddObject(other: Iterable<T>): List<T> {
        return other.filter { it !in this }
    }
}