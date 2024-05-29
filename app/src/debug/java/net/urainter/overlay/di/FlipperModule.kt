package net.urainter.overlay.di

import android.content.Context
import android.util.Log
import com.facebook.flipper.core.FlipperPlugin
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.leakcanary2.LeakCanary2FlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlipperModule {

    @Multibinds
    abstract fun provideFlipperPlugins(): Set<FlipperPlugin>

    @Binds
    @IntoSet
    @Singleton
    abstract fun provideNavigationFlipperPluginIntoSet(plugin: NavigationFlipperPlugin): FlipperPlugin

    @Binds
    @IntoSet
    @Singleton
    abstract fun provideCrashReporterPluginIntoSet(plugin: CrashReporterPlugin): FlipperPlugin

    companion object {

        @IntoSet
        @Provides
        fun provideViewInspectorPlugin(@ApplicationContext context: Context): FlipperPlugin {
            return InspectorFlipperPlugin(context, DescriptorMapping.withDefaults())
        }

        @IntoSet
        @Provides
        fun provideSharedPreferencesPlugin(@ApplicationContext context: Context): FlipperPlugin {
            return SharedPreferencesFlipperPlugin(context)
        }

        @IntoSet
        @Provides
        fun provideFlipperDatabasesPlugin(@ApplicationContext context: Context): FlipperPlugin {
            return DatabasesFlipperPlugin(context)
        }

        @IntoSet
        @Provides
        fun provideFlipperLeakCanary2FlipperPlugin(): FlipperPlugin {
            return LeakCanary2FlipperPlugin()
        }
        @Provides
        @Singleton
        fun provideFlipperNavigationPlugin(): NavigationFlipperPlugin = NavigationFlipperPlugin.getInstance()

        @Provides
        @Singleton
        fun provideFlipperCrashReporterPlugin(): CrashReporterPlugin = CrashReporterPlugin.getInstance()

        @IntoSet
        @Provides
        fun provideCrashOnErrorTree(flipperCrashReporter: CrashReporterPlugin): Timber.Tree {
            return object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.ERROR) {
                        val exception = RuntimeException("Timber e!\nTag=$tag\nMessage=$message", t)
                        flipperCrashReporter.sendExceptionMessage(
                            Thread.currentThread(),
                            exception
                        )
                    }
                }
            }
        }
    }
}
