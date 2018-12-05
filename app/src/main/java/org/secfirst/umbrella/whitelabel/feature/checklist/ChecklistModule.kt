package org.secfirst.umbrella.whitelabel.feature.checklist

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistInteractorImp
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistPresenterImp
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.ChecklistController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.ChecklistCustomController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.ChecklistDetailController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.DashboardController
import javax.inject.Singleton


@Module
class ChecklistModule {

    @Provides
    internal fun provideChecklistInteractor(interactor: ChecklistInteractorImp): ChecklistBaseInteractor = interactor

    @Provides
    internal fun provideChecklistPresenter(presenter: ChecklistPresenterImp<ChecklistView, ChecklistBaseInteractor>)
            : ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor> = presenter
}

@Singleton
@Component(modules = [ChecklistModule::class,
    RepositoryModule::class,
    AppModule::class,
    AndroidInjectionModule::class])
interface ChecklistComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ChecklistComponent
    }

    fun inject(checklistController: ChecklistController)

    fun inject(dashboardController: DashboardController)

    fun inject(checklistDetailController: ChecklistDetailController)

    fun inject(checklistCustom: ChecklistCustomController)
}