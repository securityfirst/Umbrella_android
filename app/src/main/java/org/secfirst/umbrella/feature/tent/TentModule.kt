package org.secfirst.umbrella.feature.tent

import dagger.Module
import dagger.Provides
import org.secfirst.umbrella.feature.tent.interactor.TentBaseInteractor
import org.secfirst.umbrella.feature.tent.interactor.TentInteractorImp
import org.secfirst.umbrella.feature.tent.presenter.TentBasePresenter
import org.secfirst.umbrella.feature.tent.presenter.TentPresenterImp

@Module
class TentModule{

    @Provides
    internal fun provideTentInteractor(interactor: TentInteractorImp): TentBaseInteractor = interactor

    @Provides
    internal fun provideTentPresenter(presenter: TentPresenterImp<TentView, TentBaseInteractor>)
            : TentBasePresenter<TentView, TentBaseInteractor> = presenter
}