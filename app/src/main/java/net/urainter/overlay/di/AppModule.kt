package net.urainter.overlay.di

import android.content.Context
import android.content.pm.ApplicationInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import kotlinx.coroutines.Dispatchers
import net.urainter.overlay.di.qualifiers.IsDebuggable
import timber.log.Timber
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SyncInitializers

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SecondSyncInitializers

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AsyncInitializers

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @SyncInitializers
    @Multibinds
    abstract fun syncInitializers(): Set<() -> Unit>

    @SecondSyncInitializers
    @Multibinds
    abstract fun secondSyncInitializers(): Set<() -> Unit>

    @AsyncInitializers
    @Multibinds
    abstract fun asyncInitializers(): Set<() -> Unit>

    @Multibinds
    abstract fun timberTrees(): Set<Timber.Tree>

    companion object {

        @AsyncInitializers
        @IntoSet
        @Provides
        fun initMainDispatcher(): () -> Unit = {
            Dispatchers.Main
        }

        @IsDebuggable
        @Provides
        fun provideIsDebuggable(@ApplicationContext context: Context): Boolean {
            return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        }
    }
}
