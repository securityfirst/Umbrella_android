package org.secfirst.umbrella.whitelabel.feature.difficulty

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyInteractorImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.presenter.DifficultyBasePresenter
import org.secfirst.umbrella.whitelabel.feature.difficulty.presenter.DifficultyPresenterImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyController
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView
import javax.inject.Singleton


@Module
class DifficultyModule {

    @Provides
    internal fun provideDifficultyInteractor(interactor: DifficultyInteractorImp): DifficultyBaseInteractor = interactor

    @Provides
    internal fun provideDifficultyPresenter(presenter: DifficultyPresenterImp<DifficultyView, DifficultyBaseInteractor>)
            : DifficultyBasePresenter<DifficultyView, DifficultyBaseInteractor> = presenter
}

@Singleton
@Component(modules = [DifficultyModule::class,
    RepositoryModule::class,
    AppModule::class,
    AndroidInjectionModule::class])
interface DifficultyComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): DifficultyComponent
    }

    fun inject(difficultyController: DifficultyController)
}