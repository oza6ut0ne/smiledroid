package net.urainter.overlay.di

typealias NoWildcardSet<T> = @JvmSuppressWildcards Set<T>
typealias NoWildcardMap<K, V> = @JvmSuppressWildcards Map<K, V>
typealias NoWildcardLazy<T> = @JvmSuppressWildcards dagger.Lazy<T>
