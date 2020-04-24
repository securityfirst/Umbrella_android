package org.secfirst.umbrella.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.secfirst.umbrella.feature.main.MainActivity


@Module
abstract class ActivityBuilder {

    //@ContributesAndroidInjector(modules = [(MainAtivityModule::class)])
    @ContributesAndroidInjector()
    abstract fun bindBaseActivity(): MainActivity
}

