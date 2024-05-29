package net.urainter.overlay

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.urainter.overlay.di.AsyncInitializers
import net.urainter.overlay.di.NoWildcardSet
import net.urainter.overlay.di.SecondSyncInitializers
import net.urainter.overlay.di.SyncInitializers
import timber.log.Timber
import javax.inject.Inject

private typealias InitializerFunction = () -> @JvmSuppressWildcards Unit

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    internal fun plantTimberTrees(trees: NoWildcardSet<Timber.Tree>) {
        Timber.plant(*trees.toTypedArray())
    }

    @Inject
    internal fun asyncInits(@AsyncInitializers asyncInitializers: NoWildcardSet<InitializerFunction>) {
        CoroutineScope(Dispatchers.IO).launch {
            asyncInitializers.forEach { it() }
        }
    }

    @Inject
    internal fun syncInits(@SyncInitializers initializers: NoWildcardSet<InitializerFunction>) {
        initializers.forEach { it() }
    }

    @Inject
    internal fun secondSyncInits(@SecondSyncInitializers initializers: NoWildcardSet<InitializerFunction>) {
        initializers.forEach { it() }
    }
}
