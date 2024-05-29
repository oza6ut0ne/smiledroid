package net.urainter.overlay.di

import android.content.Context
import net.urainter.overlay.BuildConfig
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperPlugin
import com.facebook.flipper.plugins.leakcanary2.FlipperLeakEventListener
import com.facebook.soloader.SoLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import leakcanary.LeakCanary
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object DebugAppModule {

    @IntoSet
    @Provides
    fun provideDebugTree(): Timber.Tree = Timber.DebugTree()

    @SecondSyncInitializers
    @IntoSet
    @Provides
    fun initFlipper(
        @ApplicationContext context: Context,
        flipperPlugins: NoWildcardSet<FlipperPlugin>
    ): () -> Unit = {
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            LeakCanary.config = LeakCanary.config.run {
                copy(eventListeners = eventListeners + FlipperLeakEventListener())
            }

            SoLoader.init(context, false)
            AndroidFlipperClient.getInstance(context).apply {
                flipperPlugins.forEach(::addPlugin)
            }.start()
        }
    }
}
