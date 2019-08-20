package org.secfirst.umbrella

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowLog
import com.raizlabs.android.dbflow.config.FlowManager
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import net.sqlcipher.database.SQLiteDatabase
import org.secfirst.advancedsearch.util.mvp.BgUiThreadSpec
import org.secfirst.advancedsearch.util.mvp.ThreadSpec
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.SQLCipherHelperImpl
import org.secfirst.umbrella.data.disk.IsoCountry
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper.Companion.EXTRA_LANGUAGE
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper.Companion.PREF_NAME
import org.secfirst.umbrella.di.component.DaggerAppComponent
import org.secfirst.umbrella.misc.setLocale
import java.util.concurrent.Executors
import javax.inject.Inject


class UmbrellaApplication : Application(), HasActivityInjector {

    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = activityDispatchingAndroidInjector

    companion object {
        lateinit var instance: UmbrellaApplication
            private set
    }

    val threadSpec: ThreadSpec = BgUiThreadSpec(Executors.newFixedThreadPool(10))

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        instance = this
        if (checkPassword()) initDatabase()
        initDaggerComponent()
        initFonts()
        initDefaultLocation()
    }

    fun checkPassword(): Boolean {
        val application = UmbrellaApplication.instance
        SQLiteDatabase.loadLibs(application)
        return try {
            val db = SQLiteDatabase.openOrCreateDatabase(application
                    .getDatabasePath(AppDatabase.NAME + ".db").path, AppDatabase.DEFAULT, null)
            db.isOpen
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }

    private fun initDefaultLocation() {
        val preference = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isoCountry = preference.getString(EXTRA_LANGUAGE, IsoCountry.ENGLISH.value)
                ?: IsoCountry.ENGLISH.value
        setLocale(isoCountry)
    }
}
