package org.secfirst.umbrella.feature.search

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.feature.search.interactor.SearchBaseInteractor
import org.secfirst.umbrella.feature.search.interactor.SearchInteractorImp
import org.secfirst.umbrella.feature.search.presenter.SearchBasePresenter
import org.secfirst.umbrella.feature.search.presenter.SearchPresenterImp
import org.secfirst.umbrella.feature.search.view.SearchController
import org.secfirst.umbrella.feature.search.view.SearchView
import javax.inject.Singleton

@Module
class SearchModule {
    
        @Provides
        internal fun provideSearchInteractor(interactor: SearchInteractorImp): SearchBaseInteractor = interactor
    
        @Provides
        internal fun provideSearchPresenter(presenter: SearchPresenterImp<SearchView, SearchBaseInteractor>)
                : SearchBasePresenter<SearchView, SearchBaseInteractor> = presenter
    }

@Singleton
@Component(modules = [
        SearchModule::class,
        AndroidInjectionModule::class
            ])
interface SearchComponent {
        @Component.Builder
        interface Builder {
        
                @BindsInstance
                fun application(application: Application): Builder
        
                fun build(): SearchComponent
            }
    
        fun inject(searchController: SearchController)
    }