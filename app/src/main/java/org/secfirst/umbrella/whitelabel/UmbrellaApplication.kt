package org.secfirst.umbrella.whitelabel

import android.app.Activity
import android.app.Application
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowLog
import com.raizlabs.android.dbflow.config.FlowManager
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import net.sqlcipher.database.SQLiteDatabase
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.SQLCipherHelperImpl
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.di.component.DaggerAppComponent
import javax.inject.Inject


class UmbrellaApplication : Application(), HasActivityInjector {

    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = activityDispatchingAndroidInjector

    companion object {
        lateinit var instance: UmbrellaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        instance = this
        val shared = getSharedPreferences(AppPreferenceHelper.PREF_NAME, MODE_PRIVATE)
        val isLogged = shared.getBoolean(AppPreferenceHelper.EXTRA_LOGGED_IN, false)
        if (!isLogged) initDatabase()
        initDaggerComponent()
        initFonts()
        initFabric()
    }

    private fun initDaggerComponent() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }

    private fun initDatabase() {
        SQLiteDatabase.loadLibs(this)
        val dbConfig = FlowConfig.Builder(this)
                .addDatabaseConfig(DatabaseConfig
                        .Builder(AppDatabase::class.java)
                        .databaseName(AppDatabase.NAME)
                        .openHelper { databaseDefinition, helperListener ->
                            SQLCipherHelperImpl(databaseDefinition, helperListener)
                        }
                        .build())
                .build()
        FlowManager.init(dbConfig)
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V)
    }

    private fun initFonts() {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
    }

    private fun initFabric() {
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().build())
                .build()
        val fabric = Fabric.Builder(this)
                .kits(crashlyticsKit)
                .debuggable(true)
                .build()
        Fabric.with(fabric)
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}
