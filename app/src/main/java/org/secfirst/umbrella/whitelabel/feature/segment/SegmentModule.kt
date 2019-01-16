package org.secfirst.umbrella.whitelabel.feature.segment

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentInteractorImp
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentPresenterImp
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView
import org.secfirst.umbrella.whitelabel.feature.segment.view.controller.HostSegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.controller.SegmentController
import javax.inject.Singleton


@Module
class SegmentModule {

    @Provides
    internal fun provideSegmentInteractor(interactor: SegmentInteractorImp): SegmentBaseInteractor = interactor

    @Provides
    internal fun provideSegmentPresenter(presenter: SegmentPresenterImp<SegmentView, SegmentBaseInteractor>)
            : SegmentBasePresenter<SegmentView, SegmentBaseInteractor> = presenter
}

@Singleton
@Component(modules = [SegmentModule::class,
    RepositoryModule::class,
    AppModule::class,
    AndroidInjectionModule::class])
interface SegmentComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): SegmentComponent
    }

    fun inject(segmentController: SegmentController)

    fun inject(hostSegmentController: HostSegmentController)
}