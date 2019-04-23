package org.secfirst.umbrella.feature.lesson

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.di.module.AppModule
import org.secfirst.umbrella.di.module.RepositoryModule
import org.secfirst.umbrella.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.feature.lesson.interactor.LessonInteractorImp
import org.secfirst.umbrella.feature.lesson.presenter.LessonBasePresenter
import org.secfirst.umbrella.feature.lesson.presenter.LessonPresenterImp
import org.secfirst.umbrella.feature.lesson.view.LessonView
import org.secfirst.umbrella.feature.lesson.view.LessonController
import javax.inject.Singleton

@Module
class LessonModule {

    @Provides
    internal fun provideLessonInteractor(interactor: LessonInteractorImp): LessonBaseInteractor = interactor

    @Provides
    internal fun provideLessonPresenter(presenter: LessonPresenterImp<LessonView, LessonBaseInteractor>)
            : LessonBasePresenter<LessonView, LessonBaseInteractor> = presenter
}

@Singleton
@Component(modules = [LessonModule::class,
    RepositoryModule::class,
    AppModule::class,
    AndroidInjectionModule::class])
interface LessonComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): LessonComponent
    }

    fun inject(lessonController: LessonController)
}