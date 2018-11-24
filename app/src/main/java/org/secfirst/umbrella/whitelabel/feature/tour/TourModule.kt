package org.secfirst.umbrella.whitelabel.feature.tour

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.whitelabel.di.builder.ActivityBuilder
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.NetworkModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.di.module.TentContentModule
import org.secfirst.umbrella.whitelabel.feature.content.ContentModule
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
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
