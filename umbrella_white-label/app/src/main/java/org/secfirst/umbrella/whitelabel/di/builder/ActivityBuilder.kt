package org.secfirst.umbrella.whitelabel.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.secfirst.umbrella.whitelabel.feature.MainActivity


@Module
abstract class ActivityBuilder {

    //@ContributesAndroidInjector(modules = [(MainAtivityModule::class)])
    @ContributesAndroidInjector()
    abstract fun bindBaseActivity(): MainActivity
}

