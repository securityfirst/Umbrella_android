package org.secfirst.umbrella.whitelabel.feature.login.view

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.whitelabel.di.module.AppModule
import org.secfirst.umbrella.whitelabel.di.module.RepositoryModule
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginInteractorImp
import org.secfirst.umbrella.whitelabel.feature.login.presenter.LoginBasePresenter
import org.secfirst.umbrella.whitelabel.feature.login.presenter.LoginPresenterImp
import javax.inject.Singleton

@Module
class LoginModule {

    @Provides
    internal fun provideLoginInteractor(interactor: LoginInteractorImp): LoginBaseInteractor = interactor

    @Provides
    internal fun provideLoginPresenter(presenter: LoginPresenterImp<LoginView, LoginBaseInteractor>)
            : LoginBasePresenter<LoginView, LoginBaseInteractor> = presenter
}

@Singleton
@Component(modules = [LoginModule::class,
    RepositoryModule::class,
    AppModule::class,
    AndroidInjectionModule::class])
interface LoginComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): LoginComponent
    }

    fun inject(loginController: LoginController)
}