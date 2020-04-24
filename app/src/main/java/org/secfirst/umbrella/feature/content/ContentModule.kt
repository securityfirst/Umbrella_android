package org.secfirst.umbrella.feature.content

import dagger.Module
import dagger.Provides
import org.secfirst.umbrella.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.feature.content.interactor.ContentInteractorImp
import org.secfirst.umbrella.feature.content.presenter.ContentBasePresenter
import org.secfirst.umbrella.feature.content.presenter.ContentPresenterImp


@Module
class ContentModule {

    @Provides
    internal fun provideContentInteractor(interactor: ContentInteractorImp): ContentBaseInteractor = interactor

    @Provides
    internal fun provideContentPresenter(presenter: ContentPresenterImp<ContentView, ContentBaseInteractor>)
            : ContentBasePresenter<ContentView, ContentBaseInteractor> = presenter
}
