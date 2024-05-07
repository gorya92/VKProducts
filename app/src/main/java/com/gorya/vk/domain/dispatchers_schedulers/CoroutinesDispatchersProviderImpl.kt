package com.gorya.vk.domain.dispatchers_schedulers

import com.gorya.vk.domain.dispatchers_schedulers.CoroutinesDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoroutinesDispatchersProviderImpl @Inject constructor() : CoroutinesDispatchersProvider {
  override val io: CoroutineDispatcher = Dispatchers.IO
  override val main: CoroutineDispatcher = Dispatchers.Main
}
