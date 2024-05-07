package com.gorya.vk.domain.dispatchers_schedulers

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutinesDispatchersProvider {
  val io: CoroutineDispatcher
  val main: CoroutineDispatcher
}
