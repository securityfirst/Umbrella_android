package org.secfirst.umbrella.whitelabel.di.module

import android.app.Application
import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import org.secfirst.umbrella.whitelabel.data.VirtualStorage
import org.secfirst.umbrella.whitelabel.data.database.content.ContentDao
import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepository
import org.secfirst.umbrella.whitelabel.data.database.form.FormDao
import org.secfirst.umbrella.whitelabel.data.database.form.FormRepo
import org.secfirst.umbrella.whitelabel.data.database.form.FormRepository
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonDao
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepository
import org.secfirst.umbrella.whitelabel.data.database.reader.RssDao
import org.secfirst.umbrella.whitelabel.data.database.reader.RssRepo
import org.secfirst.umbrella.whitelabel.data.database.reader.RssRepository
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig
import org.secfirst.umbrella.whitelabel.data.disk.TentDao
import org.secfirst.umbrella.whitelabel.data.disk.TentRepo
import org.secfirst.umbrella.whitelabel.data.disk.TentRepository
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.network.NetworkEndPoint.BASE_URL
import org.secfirst.umbrella.whitelabel.serialize.ElementLoader
import org.secfirst.umbrella.whitelabel.serialize.ElementSerializer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class AppModule {
    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context = application

    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @Singleton
    internal fun provideElementSerializer(tentRep: TentRepo) = ElementSerializer(tentRep)

    @Provides
    @Singleton
    internal fun provideElementLoader(tentRep: TentRepo) = ElementLoader(tentRep)
}

@Module
class TentContentModule {
    internal val tentDao
        get() = object : TentDao {}

    @Provides
    @Singleton
    internal fun provideTentConfig(context: Context) = TentConfig(context.cacheDir.path + "/repo/",
            context.cacheDir.path + "/resources/")

    @Provides
    @Singleton
    internal fun provideTentRepo(tentConfig: TentConfig): TentRepo = TentRepository(tentDao, tentConfig)
}


@Module
class RepositoryModule {

    internal val rssDao
        get() = object : RssDao {}

    internal val contentDao
        get() = object : ContentDao {}

    internal val formDao
        get() = object : FormDao {}

    internal val lessonDao
        get() = object : LessonDao {}

    @Provides
    @Singleton
    internal fun provideLessonDao(): LessonRepo = LessonRepository(lessonDao)

    @Provides
    @Singleton
    internal fun provideContentDao(): ContentRepo = ContentRepository(contentDao)

    @Provides
    @Singleton
    internal fun provideFormDao(): FormRepo = FormRepository(formDao)

    @Provides
    @Singleton
    internal fun provideVirtualStorage(application: Application): VirtualStorage = VirtualStorage(application)

    @Provides
    @Singleton
    internal fun provideReaderRepo(): RssRepo = RssRepository(rssDao)

}

@Module
class NetworkModule {

    @Provides
    @Reusable
    internal fun providePostApi(retrofit: Retrofit): ApiHelper {
        return retrofit.create(ApiHelper::class.java)
    }

    @Provides
    @Reusable
    internal fun provideRetrofitInterface(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
    }
}
