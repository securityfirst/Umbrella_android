package org.secfirst.umbrella.feature.tour

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.di.builder.ActivityBuilder
import org.secfirst.umbrella.di.module.AppModule
import org.secfirst.umbrella.di.module.NetworkModule
import org.secfirst.umbrella.di.module.RepositoryModule
import org.secfirst.umbrella.di.module.TentContentModule
import org.secfirst.umbrella.feature.content.ContentModule
import org.secfirst.umbrella.feature.tour.view.TourController
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class),
    AppModule::class,
    RepositoryModule::class,
    ContentModule::class,
    TentContentModule::class,
    NetworkModule::class,
    ActivityBuilder::class])
interface TourComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): TourComponent
    }

    fun inject(tourController: TourController)
}
