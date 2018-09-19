package org.secfirst.umbrella.whitelabel.feature.reader

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import org.secfirst.umbrella.whitelabel.di.builder.ActivityBuilder
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.NetworkModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderInteractorImp
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderPresenterImp
import org.secfirst.umbrella.whitelabel.feature.reader.view.controller.RssController
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import javax.inject.Singleton


@Module
class ReaderModule {

    @Provides
    internal fun provideReaderInteractor(interactor: ReaderInteractorImp): ReaderBaseInteractor = interactor

    @Provides
    internal fun provideReaderPresenter(presenter: ReaderPresenterImp<ReaderView, ReaderBaseInteractor>)
            : ReaderBasePresenter<ReaderView, ReaderBaseInteractor> = presenter
}

@Singleton
@Component(modules = [ReaderModule::class,
    (AppModule::class),
    (RepositoryModule::class),
    (NetworkModule::class),
    (ActivityBuilder::class)])
interface ReanderComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ReanderComponent
    }

    fun inject(rssController: RssController)
}
