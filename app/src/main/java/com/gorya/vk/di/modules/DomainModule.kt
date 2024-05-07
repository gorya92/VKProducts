package com.gorya.vk.di.modules

import com.gorya.vk.data.ProductsRepositoryImpl
import com.gorya.vk.domain.dispatchers_schedulers.CoroutinesDispatchersProvider
import com.gorya.vk.domain.dispatchers_schedulers.CoroutinesDispatchersProviderImpl
import com.gorya.vk.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {
  @Binds
  fun provideCoroutinesDispatchersProvider(coroutinesDispatchersProviderImpl: CoroutinesDispatchersProviderImpl): CoroutinesDispatchersProvider


  @Binds
  fun provideProductRepository(productRepositoryImpl: ProductsRepositoryImpl): ProductRepository
}
