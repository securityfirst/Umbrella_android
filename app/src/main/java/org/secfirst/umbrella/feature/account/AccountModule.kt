package org.secfirst.umbrella.feature.account

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.di.module.AppModule
import org.secfirst.umbrella.di.module.NetworkModule
import org.secfirst.umbrella.di.module.RepositoryModule
import org.secfirst.umbrella.di.module.TentContentModule
import org.secfirst.umbrella.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.feature.account.interactor.AccountInteractorImp
import org.secfirst.umbrella.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.feature.account.presenter.AccountPresenterImp
import org.secfirst.umbrella.feature.account.view.AccountController
import org.secfirst.umbrella.feature.account.view.AccountView
import org.secfirst.umbrella.feature.account.view.SettingsController
import org.secfirst.umbrella.feature.content.ContentModule
import org.secfirst.umbrella.feature.tent.TentModule
import javax.inject.Singleton

@Module
class AccountModule {

    @Provides
    internal fun provideAccountInteractor(interactor: AccountInteractorImp): AccountBaseInteractor = interactor

    @Provides
    internal fun provideAccountPresenter(presenter: AccountPresenterImp<AccountView, AccountBaseInteractor>)
            : AccountBasePresenter<AccountView, AccountBaseInteractor> = presenter
}

@Singleton
@Component(modules = [AccountModule::class,
    RepositoryModule::class,
    AppModule::class,
    ContentModule::class,
    TentModule::class,
    TentContentModule::class,
    NetworkModule::class,
    AndroidInjectionModule::class])
interface AccountComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AccountComponent
    }

    fun inject(accountController: AccountController)

    fun inject(settingsController: SettingsController)

}