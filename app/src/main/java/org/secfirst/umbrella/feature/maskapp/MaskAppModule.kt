package org.secfirst.umbrella.feature.maskapp

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.di.module.AppModule
import org.secfirst.umbrella.di.module.NetworkModule
import org.secfirst.umbrella.di.module.RepositoryModule
import org.secfirst.umbrella.feature.maskapp.interactor.MaskAppBaseInteractor
import org.secfirst.umbrella.feature.maskapp.interactor.MaskAppInteractorImp
import org.secfirst.umbrella.feature.maskapp.presenter.MaskAppBasePresenter
import org.secfirst.umbrella.feature.maskapp.presenter.MaskAppPresenterImp
import org.secfirst.umbrella.feature.maskapp.view.CalculatorController
import org.secfirst.umbrella.feature.maskapp.view.MaskAppView
import javax.inject.Singleton


@Module
class MaskAppModule {

    @Provides
    internal fun provideMaskAppInteractor(interactor: MaskAppInteractorImp): MaskAppBaseInteractor = interactor

    @Provides
    internal fun provideMaskAppPresenter(presenter: MaskAppPresenterImp<MaskAppView, MaskAppBaseInteractor>)
            : MaskAppBasePresenter<MaskAppView, MaskAppBaseInteractor> = presenter
}

@Singleton
@Component(modules = [MaskAppModule::class,
    RepositoryModule::class,
    AppModule::class,
    NetworkModule::class,
    AndroidInjectionModule::class])

interface MaskAppComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): MaskAppComponent
    }

    fun inject(calculatorController: CalculatorController)
}