package com.gorya.vk.ui.main

import com.gorya.vk.ui.main.MainContract
import com.gorya.vk.ui.main.MainInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface MainModule {
  @Binds
  @ViewModelScoped
  fun provideMainInteractor(impl: MainInteractorImpl): MainContract.Interactor
}
