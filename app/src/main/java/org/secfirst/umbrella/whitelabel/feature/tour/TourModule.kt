package org.secfirst.umbrella.whitelabel.feature.tour

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.whitelabel.di.builder.ActivityBuilder
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.NetworkModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.di.module.TentContentModule
import org.secfirst.umbrella.whitelabel.feature.tour.interactor.TourBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.tour.interactor.TourInteractorImp
import org.secfirst.umbrella.whitelabel.feature.tour.presenter.TourBasePresenter
import org.secfirst.umbrella.whitelabel.feature.tour.presenter.TourPresenterImp
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourView
import javax.inject.Singleton


@Module
class TourModule {

    @Provides
    internal fun provideTourInteractor(interactor: TourInteractorImp): TourBaseInteractor = interactor

    @Provides
    internal fun provideTourPresenter(presenter: TourPresenterImp<TourView, TourBaseInteractor>)
            : TourBasePresenter<TourView, TourBaseInteractor> = presenter
}

@Singleton
@Component(modules = [(AndroidInjectionModule::class),
    (AppModule::class),
    (RepositoryModule::class),
    (TentContentModule::class),
    (TourModule::class),
    (NetworkModule::class),
    (ActivityBuilder::class)])
interface TourComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): TourComponent
    }

    fun inject(tourController: TourController)
}
